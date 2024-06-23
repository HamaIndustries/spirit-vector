package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.move.MovementType;
import symbolics.division.spirit.vector.logic.move.WallJumpMovement;
import symbolics.division.spirit.vector.logic.state.ManagedState;

public class GroundPoundAbility extends AbstractSpiritVectorAbility {

    public static final float SLAM_SPEED = 2f;
    private static final float SLAM_STORAGE_SPEED_MULTIPLIER = 1.5f;
    private static final int SLAM_STORAGE_DURATION_TICKS = 20 * 10;
    private static final double SLAM_ATTACK_RANGE_BLOCKS = 3;
    private static final Identifier CURRENTLY_SLAMMING_STATE_ID = SpiritVectorMod.id("currently_slamming");
    private static final Identifier SLAM_STORAGE_EFFECT_ID = SpiritVectorMod.id("slam_storage");

    public GroundPoundAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 10);
    }

    @Override
    public void register(SpiritVector sv) {
        sv.stateManager().register(SLAM_STORAGE_EFFECT_ID, new ManagedState(sv));
        sv.stateManager().register(CURRENTLY_SLAMMING_STATE_ID, new SlamJamState(sv));
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        SlamJamState slamJam = ((SlamJamState)sv.stateManager().getState(CURRENTLY_SLAMMING_STATE_ID));
        if (!sv.stateManager().isActive(CURRENTLY_SLAMMING_STATE_ID)) {
            slamJam.ticksActive = 0;
            return true;
        } else if (sv.user.isInFluid() || sv.user.isOnGround()) {
            requestSlamEffect(sv, slamJam.ticksActive);
            slamJam.ticksActive = 0;
            sv.stateManager().disableState(CURRENTLY_SLAMMING_STATE_ID);
            return true;
        }
        return false;
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        // ability conditions are always called if they would otherwise be allowed to run
        // making this an acceptable entrypoint for setting up state
        sv.stateManager().enableState(CURRENTLY_SLAMMING_STATE_ID);
        return true;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        if (WallJumpMovement.idealWalljumpingConditions(sv, ctx) && sv.inputManager().consume(Input.JUMP)) {
            // slam storage
            sv.stateManager().enableStateFor(SLAM_STORAGE_EFFECT_ID, SLAM_STORAGE_DURATION_TICKS);
            MovementType.WALL_JUMP.travel(sv, ctx);
            sv.stateManager().disableState(CURRENTLY_SLAMMING_STATE_ID);
        } else {
            sv.user.setVelocity(0, -SLAM_SPEED, 0);
            MovementType.NEUTRAL.travel(sv, ctx);
        }
    }

    private void requestSlamEffect(SpiritVector sv, int power) {
        SlamPacketC2S.requestSlam(power);
        Vec3d up = new Vec3d(0, 1, 0);
        for (var dir : Direction.values()) {
            sv.effectsManager().spawnRing(sv.user.getPos().offset(dir, SLAM_ATTACK_RANGE_BLOCKS), up);
        }
    }

    public static void doSlamEffect(LivingEntity entity, float power) {
        var damageSource = entity.getWorld().getDamageSources().fallingAnvil(entity);
        for (var target : entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(SLAM_ATTACK_RANGE_BLOCKS, 0, SLAM_ATTACK_RANGE_BLOCKS))) {
            System.out.println("slammage: " + power/100);
            target.damage(damageSource, power / 10);
            var delta = target.getPos().subtract(entity.getPos()).normalize();
            target.addVelocity(delta.withAxis(Direction.Axis.Y, 1).multiply((float)Math.max(power / 100, 0.1)));
        }
    }

    public static float consumeSpeedMultiplier(SpiritVector sv) {
        return sv.stateManager().getOptional(SLAM_STORAGE_EFFECT_ID).map(
                state -> { state.clearTicks(); return SLAM_STORAGE_SPEED_MULTIPLIER; }
        ).orElse(1f);
    }

    private static class SlamJamState extends ManagedState {
        public int ticksActive;
        public SlamJamState(SpiritVector sv) {
            super(sv);
        }

        @Override
        public void tick() {
            ticksActive++;
            super.tick();
        }

        @Override
        public void tickInactive() {
            ticksActive = 0;
            super.tickInactive();
        }
    }
}
