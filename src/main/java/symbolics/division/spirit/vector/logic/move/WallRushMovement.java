package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.state.ManagedState;
import symbolics.division.spirit.vector.logic.state.ParticleTrailEffectState;

/*
Mixture of sliding and walljump

when crouching at wall in midair, do sliding movement (setting to
zero speed under a certain threshold for cling) and maintain until
cooldown. possibly in future increase this at cost of momentum.
 */

public class WallRushMovement extends AbstractMovementType {
    private static Identifier WALL_CLING_STATE = SpiritVectorMod.id("wall_cling");
    private static Identifier WALL_CLING_CD_STATE = SpiritVectorMod.id("wall_cling_cd");
    private static int WALL_CLING_TICKS = 20 * 3;
    private static int WALL_CLING_COOLDOWN_TICKS = 20 * 1;
    private static float WALL_CLING_SPEED_THRESHOLD = 0.1f;
    private static float SPEED_SNAP_PROPORTION = 0.9f;
    private static boolean ready = false;

    public WallRushMovement(Identifier id) {
        super(id);
    }

    @Override
    public final void configure(SpiritVector sv) {
        sv.stateManager().register(WALL_CLING_STATE, new ManagedState(sv));
        sv.stateManager().register(WALL_CLING_CD_STATE, new ManagedState(sv));
        ready = true;
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        if (!ready) {
            throw new RuntimeException("Wall rush movement was not configured!");
        }
        // consume because we're managing with a state anyways
        if (       !sv.user.isOnGround()
                && !sv.stateManager().isActive(WALL_CLING_CD_STATE)
                && MovementUtils.idealWallrunningConditions(sv)
                && sv.inputManager().rawInput(Input.CROUCH)) { // sticky, ignores consumption
            sv.inputManager().consume(Input.CROUCH);
            sv.stateManager().enableStateFor(WALL_CLING_STATE, WALL_CLING_TICKS);
            return true;
        }
        return false;
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        boolean completed = sv.inputManager().rawInput(Input.JUMP)
                     || !sv.inputManager().rawInput(Input.CROUCH)
                     || !sv.stateManager().isActive(WALL_CLING_STATE)
                     || !MovementUtils.idealWallrunningConditions(sv);
        if (completed && !sv.stateManager().isActive(WALL_CLING_STATE)) {
            sv.stateManager().enableStateFor(WALL_CLING_CD_STATE, WALL_CLING_COOLDOWN_TICKS);
        }
        return completed;
    }

    // if one axis speed significantly higher than another, set it all to one axis.
    protected static Vec3d snapAxis(Vec3d vel) {
        double aX = Math.abs(vel.x);
        double aZ = Math.abs(vel.z);
        if (MathHelper.absMax(vel.x, vel.z) > SPEED_SNAP_PROPORTION) {
            System.out.println("snap");
            var speed = Math.sqrt(aX*aX+aZ*aZ);
            if (aX > aZ) {
                vel = new Vec3d(Math.signum(vel.x) * speed, vel.y, 0);
            } else {
                vel = new Vec3d(0, vel.y, Math.signum(vel.z) * speed);
            }
        }
        return vel;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        // No DI, you either got it you don't
        Vec3d vel = sv.user.getVelocity();
        vel = vel.withAxis(
                Direction.Axis.Y,
                vel.y > 0 ? vel.y - sv.user.getFinalGravity() : 0 // apply gravity only going up
        );
        double speed = vel.length();
        if (speed > WALL_CLING_SPEED_THRESHOLD) {
            vel = snapAxis(vel);
            sv.user.setVelocity(vel);
            sv.user.move(net.minecraft.entity.MovementType.SELF, sv.user.getVelocity());
            if (speed > SlideMovement.MIN_SPEED_FOR_TRAIL) {
                sv.stateManager().enableStateFor(ParticleTrailEffectState.ID, 1);
            }
        }
        ctx.ci().cancel();
    }

    @Override
    public void updateValues(SpiritVector sv) {
        // grants 2 momentum/sec while wallrunning as a little treat
        if (sv.user.speed > 0.001 && sv.user.age % 10 == 0) {
            sv.modifyMomentum(2);
        }
    }
}
