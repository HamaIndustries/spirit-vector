package symbolics.division.spirit_vector.logic.vector;

/*
    Core logic for individual spirit vector state
    Every entity with an SV equipped will have one of these
    associated with it.

    Make a new one when SV is equipped or modified
 */

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit_vector.SpiritVectorItems;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.logic.SVEntityState;
import symbolics.division.spirit_vector.logic.TravelMovementContext;
import symbolics.division.spirit_vector.logic.ability.AbilitySlot;
import symbolics.division.spirit_vector.logic.ability.GroundPoundAbility;
import symbolics.division.spirit_vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit_vector.logic.ability.SpiritVectorHeldAbilities;
import symbolics.division.spirit_vector.logic.input.Input;
import symbolics.division.spirit_vector.logic.input.InputManager;
import symbolics.division.spirit_vector.logic.move.MovementType;
import symbolics.division.spirit_vector.logic.move.MovementUtils;
import symbolics.division.spirit_vector.logic.move.WallJumpMovement;
import symbolics.division.spirit_vector.logic.state.ManagedState;
import symbolics.division.spirit_vector.logic.state.ParticleTrailEffectState;
import symbolics.division.spirit_vector.logic.state.StateManager;
import symbolics.division.spirit_vector.logic.state.WingsEffectState;
import symbolics.division.spirit_vector.sfx.EffectsManager;
import symbolics.division.spirit_vector.sfx.SFXPack;

public class SpiritVector {

    public static final int MAX_MOMENTUM = 100;
    public static final int MOMENTUM_FAST_THRESHOLD = MAX_MOMENTUM / 4;
    public static final float MINIMUM_SPEED_FOR_TRAIL_WHILE_SOARING = 0.2f;
    public static final Identifier MOMENTUM_DECAY_GRACE_STATE = SpiritVectorMod.id("momentum_decay_grace");

    private static final Identifier MODIFY_MOMENTUM_COOLDOWN_STATE = SpiritVectorMod.id("momentum_cd_state");

    public static SpiritVector of(LivingEntity user, ItemStack itemStack) {
        return VectorType.getFromStack(itemStack).factory().make(user, itemStack);
    }

    @Nullable
    public static ItemStack getEquippedItem(LivingEntity entity) {
        if (entity instanceof PlayerEntity player && player.isSpectator()) {
            return null;
        }
        ItemStack item = entity.getEquippedStack(EquipmentSlot.FEET);
        return item.isOf(SpiritVectorItems.SPIRIT_VECTOR) ? item : null;
    }

    public static boolean hasEquipped(LivingEntity entity) {
        return getEquippedItem(entity) != null;
    }

    public final LivingEntity user;

    protected int momentum = 0;
    protected MovementType moveState = MovementType.NEUTRAL;
    protected SpiritVectorAbility queuedAbility; // hand rune input
    protected Vec3d inputDirection = Vec3d.ZERO;
    protected Vec3d impulse = Vec3d.ZERO;
    protected final EffectsManager effectsManager;
    protected final StateManager stateManager = new StateManager();
    protected final InputManager inputManager = new InputManager();
    protected final SFXPack<?> sfx;
    protected final SpiritVectorHeldAbilities abilities;
    protected final VectorType type;

    // these are checked in order prior to rune movements
    protected final MovementType[] movements = {
            MovementType.VAULT,
            MovementType.JUMP,
            MovementType.SLIDE,
            MovementType.WALL_JUMP,
            MovementType.WALL_RUSH
    };

    public SpiritVector(LivingEntity user, ItemStack itemStack) {
        this(user, itemStack, VectorType.SPIRIT);
    }

    protected SpiritVector(LivingEntity user, ItemStack itemStack, VectorType type) {

        this.sfx = SFXPack.getFromStack(itemStack, user.getUuid());
        this.effectsManager = new EffectsManager(this);
        this.user = user;
        this.type = type;

        stateManager.register(ParticleTrailEffectState.ID, new ParticleTrailEffectState(this));
        stateManager.register(WingsEffectState.ID, new WingsEffectState(this));
        stateManager.register(MODIFY_MOMENTUM_COOLDOWN_STATE, new ManagedState(this));
        stateManager.register(MOMENTUM_DECAY_GRACE_STATE, new ManagedState(this));

        for (MovementType move : movements) {
            move.configure(this);
        }

        abilities = itemStack.getOrDefault(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities());
        for (AbilitySlot slot : AbilitySlot.values()) {
            abilities.get(slot).getMovement().configure(this);
        }

    }

    public void travel(Vec3d movementInput, CallbackInfo ci) {
        stateManager.tick();
        inputDirection = MovementUtils.movementInputToVelocity(movementInput, 1, user.getYaw());
        var ctx = new TravelMovementContext(movementInput, ci, inputDirection);

        // brake
        if ( (      user.isOnGround()
                || (getMoveState() == MovementType.WALL_RUSH && user.getVelocity().withAxis(Direction.Axis.Y, 0).lengthSquared() > 0) )
             && inputManager().rawInput(Input.SPRINT)) {
            user.setVelocity(user.getVelocity().multiply(0.5));
        }

//        MovementType prev = getMoveState();
        updateMovementType(ctx);
//        if (!prev.getID().equals(getMoveState().getID())) {
//            SpiritVectorMod.LOGGER.info("state: " + prev.getID().getPath() + " -> " + getMoveState().getID().getPath());
//        }

        moveState.travel(this, ctx);
        moveState.updateValues(this);
        this.queuedAbility = null;

        var vel = user.getVelocity();
        if (isSoaring()) {
            if (vel.length() >= MINIMUM_SPEED_FOR_TRAIL_WHILE_SOARING) {
                stateManager().enableStateFor(ParticleTrailEffectState.ID, 1);
            }
            stateManager().enableStateFor(WingsEffectState.ID, 1);
        }
    }

    private void updateMovementType(TravelMovementContext ctx) {
        if (!moveState.testMovementCompleted(this, ctx)) return;
        moveState.exit(this);

        // first check standard movements
        for (MovementType m : movements) {
            if (m.testMovementCondition(this, ctx)) {
                moveState = m;
                return;
            }
        }

        // then test if any abilities apply while in air
        if (!user.isOnGround()) {
            if (queuedAbility != null && queuedAbility.cost() < getMomentum()) {
                moveState = queuedAbility.getMovement();
                return;
            }
            for (AbilitySlot slot : AbilitySlot.values()) {
                SpiritVectorAbility ability = abilities.get(slot);
                MovementType move = ability.getMovement();
                if (
                        ability.cost() <= getMomentum()
                        && move.testMovementCondition(this, ctx)
                        && inputManager().consume(slot.input)
                ) {
                    moveState = move;
                    return;
                }
            }
        }

        moveState = MovementType.NEUTRAL;
    }

    public Vec3d getImpulse() { return impulse; }
    public void setImpulse(Vec3d impulse) { this.impulse = impulse; }

    public float getMovementSpeed() { return getMovementSpeed(0.6f); }
    public float getMovementSpeed(float slip) {
        return user.getMovementSpeed() * (0.21600002F / (slip * slip * slip)) + ((float)getMomentum() / MAX_MOMENTUM) * 0.1f;
    }

    public Vec3d getInputDirection() { return inputDirection; }

    public float getStepHeight() {
        return 1.2f;
    }

    public int getMomentum() {
//        return MAX_MOMENTUM;
        return momentum;
    }

    public void modifyMomentum(int v) {
        momentum = Math.clamp(momentum + v, 0, MAX_MOMENTUM);
    }
    public boolean modifyMomentumWithCooldown(int v, int cdTicks) {
        if (!stateManager().isActive(MODIFY_MOMENTUM_COOLDOWN_STATE)) {
            modifyMomentum(v);
            stateManager().enableStateFor(MODIFY_MOMENTUM_COOLDOWN_STATE, cdTicks);
            return true;
        }
        return false;
    }

    public SFXPack<?> getSFX() {
        return sfx;
    }

    public VectorType getType() { return type; }

    public boolean isSoaring() {
        // it means you're really cool
        return getMomentum() >= MOMENTUM_FAST_THRESHOLD;
    }

    public void onLanding() {
        if (this.user.fallDistance > 0.01) {
            resetJump();
        }
    }

    public void resetJump() {
        inputManager.update(Input.JUMP, false);
        WallJumpMovement.resetWallJumpPlane(this);
    }

    public MovementType getMoveState() { return moveState; }

    public EffectsManager effectsManager() { return effectsManager; }

    public StateManager stateManager() { return stateManager; }

    public InputManager inputManager() { return inputManager; }

    public SpiritVectorHeldAbilities heldAbilities() {
        return abilities;
    }

    public SVEntityState entityState() {
        return new SVEntityState(stateManager().isActive(WingsEffectState.ID));
    }

    public float consumeSpeedMultiplier() {
        return GroundPoundAbility.consumeSpeedMultiplier(this);
    }

    public boolean enqueueAbility(SpiritVectorAbility ability) {
        ability.getMovement().configure(this);
        queuedAbility = ability;
        return true;
    }

    public double horizontalSpeed() {
        return user.getVelocity().withAxis(Direction.Axis.Y,0).length();
    }

    public boolean fluidMovementAllowed() {
        return moveState.fluidMovementAllowed(this);
    }

    public float safeFallDistance() {
        //  broken, ground damage disabled. needs networking.
        return this.getMoveState().safeFallDistance(this);
    }
}
