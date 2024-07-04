package symbolics.division.spirit.vector.logic.move;

import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.mixin.EntityAccessor;

import java.util.List;

public final class MovementUtils {
    public static boolean isSolidWall(World world, BlockPos wallPos, Direction dir) {
        return world.getBlockState(wallPos).isSideSolid(world, wallPos, dir, SideShapeType.RIGID);
    }

    @FunctionalInterface
    private interface AnchorValidator {
        boolean validate(World world, Vec3d pos, Direction dir);
    }

    // abstraction over anchor validity for different scenarios
    public static boolean checkSurroundingAnchorConditions(World world, Vec3d pos, AnchorValidator validator, boolean requireClose) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN || dir == Direction.UP || (requireClose && !closeToSide(pos, dir))) continue;
            if (validator.validate(world, pos, dir)) {
                return true;
            }
        }
        return false;
    }

    // ensure we are on the half of this cube
    // corresponding to the given direction
    // ie, if we are on the north half of a cube, return true if dir == Direction.NORTH
    private static boolean closeToSide(Vec3d pos, Direction dir) {
        double axisComponent = pos.getComponentAlongAxis(dir.getAxis());
        double axisOffset = BlockPos.ofFloored(pos).offset(dir).toCenterPos().getComponentAlongAxis(dir.getAxis());
        return Math.abs(axisComponent - axisOffset) <= 1;
    }

    // separate wall jump/cling anchor logic
    // wall jump allows jumps off a 2-pillar up or down from foot position
    public static boolean validWallJumpAnchor(World world, Vec3d pos, Direction dir) {
        BlockPos anchorPos = BlockPos.ofFloored(pos);
        BlockPos wallPos = anchorPos.offset(dir);
        return isSolidWall(world, wallPos, dir.getOpposite()) &&
                (isSolidWall(world, wallPos.up(), dir.getOpposite()) && world.isAir(anchorPos.up()))
                ||
                (isSolidWall(world, wallPos.down(), dir.getOpposite()) && world.isAir(anchorPos.down()));

    }

    // cling only allows against 2-pillars up
    public static boolean validWallRushAnchor(World world, Vec3d pos, Direction dir) {
        BlockPos anchorPos = BlockPos.ofFloored(pos);
        BlockPos wallPos = anchorPos.offset(dir);
        return     isSolidWall(world, wallPos, dir.getOpposite())
                && isSolidWall(world, wallPos.up(), dir.getOpposite())
                && world.isAir(anchorPos.up());
    }

    // convert context to input used for wall jumps
    // normal if normally valid, and
    // orthogonal (to wall) otherwise.
    private static float AXIS_ALIGN_THRESHOLD = -(float)Math.cos(Math.PI/4-0.01); // must be < cos(pi/4) or we can't consistently choose an inverted
    @Nullable
    public static Vec3d getWalljumpingInput(SpiritVector sv, TravelMovementContext ctx) {
        var input = augmentedInput(sv, ctx);
        var inputV3f = input.toVector3f();
        var pos = sv.user.getPos();
        var world = sv.user.getWorld();
        Vec3d invertedInput = null;

        for (Direction dir : Direction.values()) {
            // check if ok for a jump
            if (dir == Direction.DOWN || dir == Direction.UP || !validWallJumpAnchor(world, pos, dir)) continue;
            // determine if return normal, or prepare to return inverted
            var normal = dir.getOpposite().getUnitVector();
            float dp = normal.dot(inputV3f);
            if (dp > 0) {
                return input;
            } else if(dp < AXIS_ALIGN_THRESHOLD || invertedInput == null) {
                invertedInput = new Vec3d(normal);
            }
        }
        return invertedInput;
    }

    public static boolean idealWalljumpingConditions(SpiritVector sv, TravelMovementContext ctx) {
        // testing: any input dir
        return checkSurroundingAnchorConditions(sv.user.getWorld(), sv.user.getPos(), MovementUtils::validWallJumpAnchor, false);
        // in case we feel like going back to directional
//        Direction[] dirs = {
//                input.getComponentAlongAxis(Direction.Axis.Z) > 0 ? Direction.NORTH : Direction.SOUTH,
//                input.getComponentAlongAxis(Direction.Axis.X) > 0 ? Direction.WEST : Direction.EAST
//        };
    }

    public static boolean idealWallrunningConditions(SpiritVector sv) {
        // similar to idealWalljumpingConditions, but checks for any wall surrounding
        // user instead of in opposite direction. Should be employed for wall running/
        // wall clinging contexts where a directional input is not necessarily considered.
        return idealWallrunningConditions(sv.user.getWorld(), sv.user.getPos());
    }

    public static boolean idealWallrunningConditions(World world, Vec3d pos) {
        return checkSurroundingAnchorConditions(world, pos, MovementUtils::validWallRushAnchor, true);
    }

    public static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        } else {
            Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
            float f = MathHelper.sin(yaw * (float) (Math.PI / 180.0));
            float g = MathHelper.cos(yaw * (float) (Math.PI / 180.0));
            return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
        }
    }

    public static Vec3d augmentedInput(SpiritVector sv, TravelMovementContext ctx) {
        // does a few things:
        // - TODO: if setting toggled, calc input from look vec always (accessibility)
        // - if zero (horizontal) directional input, use input calculated from look instead
        float minLength = 0.001f;
        if (ctx.inputDir().lengthSquared() > minLength) {
            return ctx.inputDir();
        }
        var cameraInput = sv.user.getRotationVecClient().withAxis(Direction.Axis.Y, 0);
        if (cameraInput.lengthSquared() > minLength) {
            return cameraInput.normalize();
        }
        return Vec3d.fromPolar(0, sv.user.getYaw()).normalize();
    }

    public static Vec3d stepDown(Entity entity, Vec3d result, Vec3d movement) {

        // hypothetical hitbox of entity after movement is applied
        Box hypothetical = entity.getBoundingBox().offset(result);
        // drop bottom by step height
        Box down = hypothetical.stretch(0, -entity.getStepHeight(), 0);
        // get collisions such as with other entities
        List<VoxelShape> specialCollisions = entity.getWorld().getEntityCollisions(entity, down);
        // add world border and block collisions
        List<VoxelShape> collisions = EntityAccessor.invokeFindCollisionsForMovement(entity, entity.getWorld(), specialCollisions, down);

        double stepdown = ((EntityAccessor)entity).invokeAdjustMovementForCollisions(
                new Vec3d(0, -entity.getStepHeight() - 0.001, 0), // add a little bit to filter air movement
                hypothetical,
                collisions
        ).y;

        //don't step into open air
        if (stepdown >= -entity.getStepHeight()) {
            return result.add(0, stepdown, 0);
        }
        return result;
    }
}
