package symbolics.division.spirit.vector.logic;

/*
    Core logic for individual spirit vector state
    Every entity with an SV equipped will have one of these
    associated with it.

    Make a new one when SV is equipped or modified
 */

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit.vector.SpiritVectorItems;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.logic.ability.GroundPoundAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.input.InputManager;
import symbolics.division.spirit.vector.logic.move.MovementType;
import symbolics.division.spirit.vector.logic.move.MovementUtils;
import symbolics.division.spirit.vector.logic.state.ManagedState;
import symbolics.division.spirit.vector.logic.state.ParticleTrailEffectState;
import symbolics.division.spirit.vector.logic.state.StateManager;
import symbolics.division.spirit.vector.logic.state.WingsEffectState;
import symbolics.division.spirit.vector.sfx.EffectsManager;
import symbolics.division.spirit.vector.sfx.SFXPack;

public class SpiritVector {

    public static final int MAX_FUEL = 100;
    public static final int MAX_MOMENTUM = 100;
    public static final int MOMENTUM_FAST_THRESHOLD = MAX_MOMENTUM / 4;
    public static final float MINIMUM_SPEED_FOR_TRAIL_WHILE_SOARING = 0.2f;

    private static final Identifier MODIFY_MOMENTUM_COOLDOWN_STATE = SpiritVectorMod.id("momentum_cd_state");

    @Nullable
    public static ItemStack getEquippedItem(LivingEntity entity) {
        ItemStack item = entity.getEquippedStack(EquipmentSlot.FEET);
        return item.isOf(SpiritVectorItems.SPIRIT_VECTOR) ? item : null;
    }

    public static boolean hasEquipped(LivingEntity entity) {
        return getEquippedItem(entity) != null;
    }

    private int fuel = 0;
    private int momentum = 0;
    private MovementType moveState = MovementType.NEUTRAL;
    private final EffectsManager effectsManager;
    private final StateManager stateManager = new StateManager();
    private final InputManager inputManager = new InputManager();
    private final SFXPack<?> sfx;
    private SpiritVectorAbility queuedAbility; // hand rune input

    // these are checked in order prior to rune movements
    private final MovementType[] movements = {
            MovementType.VAULT,
            MovementType.JUMP,
            MovementType.SLIDE,
            MovementType.WALL_JUMP,
            MovementType.WALL_RUSH
    };

    private final SpiritVectorHeldAbilities abilities;

    public final LivingEntity user;

    public SpiritVector(LivingEntity user, ItemStack itemStack) {
        this.sfx = SFXPack.getFromStack(itemStack, user.getUuid());
        this.effectsManager = new EffectsManager(this);
        this.user = user;
        stateManager.register(ParticleTrailEffectState.ID, new ParticleTrailEffectState(this));
        stateManager.register(WingsEffectState.ID, new WingsEffectState(this));
        stateManager.register(MODIFY_MOMENTUM_COOLDOWN_STATE, new ManagedState(this));

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
        var inputDirection = MovementUtils.movementInputToVelocity(movementInput, 1, user.getYaw());
        var ctx = new TravelMovementContext(movementInput, ci, inputDirection);
        updateMovementType(ctx);
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
        if (moveState.testMovementCompleted(this, ctx)) {
            moveState.exit(this);
            moveState = MovementType.NEUTRAL;

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
        }
    }

    public float getMovementSpeed() { return getMovementSpeed(0.6f); }
    public float getMovementSpeed(float slip) {
        return user.getMovementSpeed() * (0.21600002F / (slip * slip * slip)) + ((float)getMomentum() / MAX_MOMENTUM) * 0.1f;
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

    public int getFuel() {
        return fuel;
    }
    public void modifyFuel(int v) {
        fuel = Math.clamp(fuel + v, 0, MAX_FUEL);
    }

    public SFXPack<?> getSFX() {
        return sfx;
    }

    public boolean isSoaring() {
        // it means you're really cool
        return getMomentum() >= MOMENTUM_FAST_THRESHOLD;
    }

    public void onLanding() {
        if (this.user.fallDistance > 0.01) {
            inputManager.update(Input.JUMP, false);
        }
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

    public boolean fluidMovementAllowed() {
        return moveState.fluidMovementAllowed(this);
    }

    public float safeFallDistance() {
        //  broken, ground damage disabled. needs networking.
        return this.getMoveState().safeFallDistance(this);
    }
}
