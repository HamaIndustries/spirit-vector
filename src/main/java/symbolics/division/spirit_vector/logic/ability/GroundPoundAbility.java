package symbolics.division.spirit_vector.logic.ability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;
import symbolics.division.spirit_vector.logic.TravelMovementContext;
import symbolics.division.spirit_vector.logic.input.Input;
import symbolics.division.spirit_vector.logic.move.MovementType;
import symbolics.division.spirit_vector.logic.move.MovementUtils;
import symbolics.division.spirit_vector.logic.state.ManagedState;

public class GroundPoundAbility extends AbstractSpiritVectorAbility {

    public static final float SLAM_SPEED = 2f;
    private static final float SLAM_STORAGE_SPEED_MULTIPLIER = 2f;
    private static final int SLAM_STORAGE_DURATION_TICKS = 20 * 10;
    private static final double SLAM_ATTACK_RANGE_BLOCKS = 3;
    private static final Identifier SLAM_STORAGE_EFFECT_ID = SpiritVectorMod.id("slam_storage");

    public GroundPoundAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 10);
    }

    @Override
    public void configure(SpiritVector sv) {
        sv.stateManager().register(SLAM_STORAGE_EFFECT_ID, new ManagedState(sv));
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        if (!sv.user.isOnGround() && MovementUtils.idealWalljumpingConditions(sv, ctx) && sv.inputManager().consume(Input.JUMP)) {
            sv.stateManager().enableStateFor(SLAM_STORAGE_EFFECT_ID, SLAM_STORAGE_DURATION_TICKS);
            MovementType.WALL_JUMP.travel(sv, ctx); // do not do this during tests. do not.
            return true;
        }
        return sv.user.isInFluid() || sv.user.isOnGround();
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return true;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        sv.user.setVelocity(0, -SLAM_SPEED, 0);
        MovementType.NEUTRAL.travel(sv, ctx);
    }

    public static void requestSlamEffect(SpiritVector sv, int power) {
        SlamPacketC2S.requestSlam(power);
        Vec3d up = new Vec3d(0, 1, 0);
        for (var dir : Direction.values()) {
            sv.effectsManager().spawnRing(sv.user.getPos().offset(dir, SLAM_ATTACK_RANGE_BLOCKS), up);
        }
    }

    public static void doSlamEffect(LivingEntity entity, float power) {
        var damageSource = entity.getWorld().getDamageSources().fallingAnvil(entity);
        for (var target : entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(SLAM_ATTACK_RANGE_BLOCKS, 0, SLAM_ATTACK_RANGE_BLOCKS))) {
            target.damage(damageSource, power / 10);
            var delta = target.getPos().subtract(entity.getPos()).normalize();
            target.addVelocity(delta.withAxis(Direction.Axis.Y, 1).multiply((float)Math.max(power / 100, 0.1)));
        }
    }

    public static float consumeSpeedMultiplier(SpiritVector sv) {
        return sv.stateManager().getOptional(SLAM_STORAGE_EFFECT_ID).map(state -> {
            float m = state.isActive() ? SLAM_STORAGE_SPEED_MULTIPLIER : 1;
            state.clearTicks();
            return m;
        }).orElse(1f);
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
