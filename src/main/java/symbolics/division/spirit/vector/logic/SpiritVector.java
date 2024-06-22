package symbolics.division.spirit.vector.logic;

/*
    Abstract controller for spirit vector state
    Every entity with an SV equipped will have one of these
    associated with it.

    Make a new one when SV is re-equipped or modified
 */

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit.vector.SpiritVectorItems;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.input.InputManager;
import symbolics.division.spirit.vector.logic.move.MovementType;
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

    // these are checked in order prior to rune movements
    private final MovementType[] movements = {
            MovementType.VAULT,
            MovementType.JUMP,
            MovementType.SLIDE,
            MovementType.WALL_JUMP
    };

    private final SpiritVectorHeldAbilities abilities;

    public final LivingEntity user;

    public SpiritVector(LivingEntity user, ItemStack itemStack) {
        this.sfx = SFXPack.getFromStack(itemStack);
        this.effectsManager = new EffectsManager(this);
        this.user = user;
        stateManager.register(ParticleTrailEffectState.ID, new ParticleTrailEffectState(this));
        stateManager.register(WingsEffectState.ID, new WingsEffectState(this));

        for (MovementType move : movements) {
            move.register(this);
        }

        abilities = itemStack.getOrDefault(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities());
    }

    public void travel(Vec3d movementInput, CallbackInfo ci) {
        stateManager.tick();
        var inputDirection = SVMathHelper.movementInputToVelocity(movementInput, 1, user.getYaw());
        var ctx = new TravelMovementContext(movementInput, ci, inputDirection);
        updateMovementType(ctx);
        moveState.travel(this, ctx);
        moveState.updateValues(this);

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
                for (AbilitySlot slot : AbilitySlot.values()) {
                    SpiritVectorAbility ability = abilities.get(slot);
                    MovementType move = ability.getMovement();
                    if (
                            ability.cost() <= getMomentum()
                            && move.testMovementCompleted(this, ctx)
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
        // todo movementspeed (and maybe jump velocity) based on momentum
        return user.getMovementSpeed() * (0.21600002F / (slip * slip * slip));
//        return moveState.getMovementSpeed(this, slip);// * (1f + 1f * momentum/MAX_MOMENTUM);
    }

    public int getMomentum() {
//        return MAX_MOMENTUM;
        return momentum;
    }

    public void modifyMomentum(int v) {
        momentum = Math.clamp(momentum + v, 0, MAX_MOMENTUM);
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

    public SVEntityState entityState() {
        return new SVEntityState(stateManager().isActive(WingsEffectState.ID));
    }

}
