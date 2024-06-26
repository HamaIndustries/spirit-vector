package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
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
    private static int WALL_RUN_MOMENTUM_INC = SpiritVector.MAX_MOMENTUM / 20;
    private static float WALL_CLING_SPEED_THRESHOLD = 0.1f;
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
                && MovementUtils.idealWallrunningConditions(sv, ctx)
                && sv.inputManager().rawInput(Input.CROUCH)) { // sticky, ignores consumption
            sv.inputManager().consume(Input.CROUCH);
            sv.stateManager().enableStateFor(WALL_CLING_STATE, WALL_CLING_TICKS);
            return true;
        }
        return false;
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        boolean completed =  sv.inputManager().rawInput(Input.JUMP)
                     || !sv.inputManager().rawInput(Input.CROUCH)
                     || !sv.stateManager().isActive(WALL_CLING_STATE)
                     || !MovementUtils.idealWallrunningConditions(sv, ctx);
        if (completed) {
            sv.stateManager().enableStateFor(WALL_CLING_CD_STATE, WALL_CLING_COOLDOWN_TICKS);
        }
        return completed;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        // No DI, you either got it you don't
        Vec3d vel = new Vec3d(sv.user.getVelocity().x, 0, sv.user.getVelocity().z);
        double speed = vel.length();
        sv.user.setVelocity(vel);
        if (speed > WALL_CLING_SPEED_THRESHOLD) {
            sv.user.move(net.minecraft.entity.MovementType.SELF, sv.user.getVelocity());
            if (speed > SlideMovement.MIN_SPEED_FOR_TRAIL) {
                sv.stateManager().enableStateFor(ParticleTrailEffectState.ID, 1);
            }
        }
        ctx.ci().cancel();
    }

    @Override
    public void updateValues(SpiritVector sv) {
        // running increases momentum, clinging preserves
    }
}
