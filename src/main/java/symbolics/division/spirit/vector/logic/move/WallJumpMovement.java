package symbolics.division.spirit.vector.logic.move;

import net.minecraft.block.SideShapeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import symbolics.division.spirit.vector.logic.MovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;

public class WallJumpMovement extends BaseMovement {
    public WallJumpMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, MovementContext ctx) {
        if (!sv.user.isOnGround() && ctx.jumping && ctx.inputDir.length() > 0) {
            var pos = sv.user.getBlockPos();
            Direction[] dirs = {
                    ctx.inputDir.getComponentAlongAxis(Direction.Axis.Z) > 0 ? Direction.NORTH : Direction.SOUTH,
                    ctx.inputDir.getComponentAlongAxis(Direction.Axis.X) > 0 ? Direction.WEST : Direction.EAST
            };

            var world = sv.user.getWorld();
            for (Direction dir : dirs) {
                BlockPos wallPos = pos.offset(dir);
                if (isSolidWall(world, wallPos, dir.getOpposite())
                            && (  isSolidWall(world, wallPos.up(), dir.getOpposite())
                               || isSolidWall(world, wallPos.down(), dir.getOpposite()))
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSolidWall(World world, BlockPos wallPos, Direction dir) {
        return world.getBlockState(wallPos).isSideSolid(world, wallPos, dir, SideShapeType.RIGID);
    }

    @Override
    public void travel(SpiritVector sv, MovementContext ctx) {
        Vec3d motion = new Vec3d(ctx.inputDir.x, 0.6, ctx.inputDir.z);
        sv.user.setVelocity(motion);
        sv.getEffectsManager().spawnRing(sv.user.getWorld(), sv.user.getPos(), motion);
    }

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(5);
    }
}
