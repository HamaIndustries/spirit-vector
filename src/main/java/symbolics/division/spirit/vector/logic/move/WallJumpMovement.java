package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.state.ManagedState;
import symbolics.division.spirit.vector.logic.vector.VectorType;

public class WallJumpMovement extends AbstractMovementType {
    protected static final int MOMENTUM_GAINED = SpiritVector.MAX_MOMENTUM / 20;
    protected static final float AXIS_ALIGN_THRESHOLD = -(float)Math.cos(Math.PI/4-0.01); // must be < cos(pi/4) or we can't consistently choose an inverted
    protected static final Identifier WALL_JUMP_PLANE_TRACKER = SpiritVectorMod.id("wall_jump_plane_tracker");

    private static class WallJumpPlaneTracker extends ManagedState {
        public Pair<Direction, Integer> prevPlane;
        public WallJumpPlaneTracker(SpiritVector sv) {
            super(sv);
        }

        public boolean allowable(Direction dir, Vec3d pos) {
            return prevPlane == null
                    || dir != prevPlane.getLeft()
                    || (int)pos.getComponentAlongAxis(dir.getAxis()) != prevPlane.getRight();
        }

        public void set(Direction dir, int v) {
            this.prevPlane = new Pair<>(dir, v);
        }

        public void clear() {
            this.prevPlane = null;
        }
    }

    public static void resetWallJumpPlane(SpiritVector sv) {
        ((WallJumpPlaneTracker)sv.stateManager().getState(WALL_JUMP_PLANE_TRACKER)).clear();
    }

    // convert context to input used for wall jumps
    // normal if normally valid, and
    // orthogonal (to wall) otherwise.
    protected static Pair<Vec3d, Direction> getWalljumpingInput(SpiritVector sv, TravelMovementContext ctx) {
        Vec3d input = MovementUtils.augmentedInput(sv, ctx);
        Vector3f inputV3f = input.toVector3f();
        Vec3d pos = sv.user.getPos().add(0, 0.5, 0);

        World world = sv.user.getWorld();
        WallJumpPlaneTracker planeState = (WallJumpPlaneTracker)sv.stateManager().getState(WALL_JUMP_PLANE_TRACKER);

        Vec3d invertedInput = null;
        Direction invertedDir = null;

        for (Direction dir : Direction.values()) {
            // check if ok for a jump
            if (dir == Direction.DOWN || dir == Direction.UP
                    || !MovementUtils.validWallJumpAnchor(world, pos, dir)
                    || !planeState.allowable(dir, pos)
            ) continue;

            // determine if return normal input, or prepare to return inverted
            var normal = dir.getOpposite().getUnitVector();
            float dp = normal.dot(inputV3f);
            if (dp > 0) {
                return new Pair<>(input, dir);
            } else if(dp < AXIS_ALIGN_THRESHOLD) {
                invertedInput = new Vec3d(normal);
                invertedDir = dir;
            }
        }

        if (invertedInput != null) {
            return new Pair<>(invertedInput, invertedDir);
        } else {
            return null;
        }
    }

    public WallJumpMovement(Identifier id) {
        super(id);
    }

    @Override
    public void configure(SpiritVector sv) {
        sv.stateManager().register(WALL_JUMP_PLANE_TRACKER, new WallJumpPlaneTracker(sv));
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return !sv.user.isOnGround()
                && MovementUtils.idealWalljumpingConditions(sv, ctx)
                && sv.inputManager().consume(Input.JUMP);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
//        var input = MovementUtils.augmentedInput(sv, ctx);
        var result = getWalljumpingInput(sv, ctx);
        if (result == null) { // input invalid
            MovementType.NEUTRAL.travel(sv, ctx);
            return;
        }

        Direction dir = result.getRight();
        Vec3d input = result.getLeft();

        Vec3d motion = new Vec3d(input.x/2, 0.5, input.z/2);
        if (sv.getMoveState() == this) { // only apply for normal walljumps
            motion = motion.multiply(sv.consumeSpeedMultiplier());
        }

        if (sv.getType().equals(VectorType.DREAM)) {
            sv.user.addVelocity(motion);
        } else {
            sv.user.setVelocity(motion);
        }

        ((WallJumpPlaneTracker)sv.stateManager().getState(WALL_JUMP_PLANE_TRACKER)).set(
                dir, (int)sv.user.getPos().getComponentAlongAxis(dir.getAxis())
        );
        sv.effectsManager().spawnRing(sv.user.getPos(), motion);
    }

    @Override
    public void updateValues(SpiritVector sv) {
        if (sv.getType().equals(VectorType.SPIRIT)) {
            sv.modifyMomentum(MOMENTUM_GAINED);
            sv.stateManager().enableStateFor(SpiritVector.MOMENTUM_DECAY_GRACE_STATE, 20);
        }
    }
}
