package symbolics.division.spirit.vector.logic.move;

import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;

public final class MovementUtils {
    public static boolean isSolidWall(World world, BlockPos wallPos, Direction dir) {
        return world.getBlockState(wallPos).isSideSolid(world, wallPos, dir, SideShapeType.RIGID);
    }

    public static boolean validWallAnchor(World world, BlockPos playerPos, Direction dir) {
        // is valid for jumping/running/clinging to at this position,
        // pointing in this direction (from player to side)
        // must be 2 blocks tall up or down (otherwise it would be too small
        // to target consistently vs ledging, bad ux)
        BlockPos wallPos = playerPos.offset(dir);
        return isSolidWall(world, wallPos, dir.getOpposite()) &&
                (isSolidWall(world, wallPos.up(), dir.getOpposite()) && world.isAir(playerPos.up()))
                ||
                (isSolidWall(world, wallPos.down(), dir.getOpposite()) && world.isAir(playerPos.down()));
    }

    public static boolean idealWalljumpingConditions(SpiritVector sv, TravelMovementContext ctx) {
        var input = augmentedInput(sv, ctx);
        var pos = sv.user.getBlockPos();
        Direction[] dirs = {
                input.getComponentAlongAxis(Direction.Axis.Z) > 0 ? Direction.NORTH : Direction.SOUTH,
                input.getComponentAlongAxis(Direction.Axis.X) > 0 ? Direction.WEST : Direction.EAST
        };

        var world = sv.user.getWorld();
        for (Direction dir : dirs) {
            if (validWallAnchor(world, pos, dir)) {
                return true;
            }
        }
        return false;
    }

    public static boolean idealWallrunningConditions(SpiritVector sv) {
        // similar to idealWalljumpingConditions, but checks for any wall surrounding
        // user instead of in opposite direction. Should be employed for wall running/
        // wall clinging contexts where a directional input is not necessarily considered.
        return idealWallrunningConditions(sv.user.getWorld(), sv.user.getBlockPos());
    }

    public static boolean idealWallrunningConditions(World world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN || dir == Direction.UP) continue;
            if (validWallAnchor(world, pos, dir)) {
                return true;
            }
        }
        return false;
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
