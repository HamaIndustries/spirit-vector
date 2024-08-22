package symbolics.division.spirit.vector.logic.move;

import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;

public final class MovementUtils {
    public static boolean isSolidWall(World world, BlockPos wallPos, Direction dir) {
        return world.getBlockState(wallPos).isSideSolid(world, wallPos, dir, SideShapeType.RIGID);
    }

    @FunctionalInterface
    private interface AnchorValidator {
        boolean validate(World world, Vec3d pos, Direction dir);
    }

    // abstraction over anchor validity for different scenarios
    private static boolean checkSurroundingAnchorConditions(World world, Vec3d pos, AnchorValidator validator, boolean requireClose) {
        pos = pos.add(0, 0.5, 0); // assume we are given players feet position
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

    public static boolean idealWalljumpingConditions(SpiritVector sv, TravelMovementContext ctx) {
        // testing: any input dir
        return checkSurroundingAnchorConditions(
                sv.user.getWorld(),
                sv.user.getPos(),
                MovementUtils::validWallJumpAnchor,
                false
        );
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

}
