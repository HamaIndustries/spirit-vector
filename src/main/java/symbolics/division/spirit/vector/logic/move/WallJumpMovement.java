package symbolics.division.spirit.vector.logic.move;

import net.minecraft.block.SideShapeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
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
                if (
                        world.getBlockState(wallPos).isSideSolid(world, wallPos, dir.getOpposite(), SideShapeType.RIGID)
                        && world.getBlockState(wallPos.up()).isSideSolid(world, wallPos, dir.getOpposite(), SideShapeType.RIGID)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void travel(SpiritVector sv, MovementContext ctx) {
        sv.user.setVelocity(ctx.inputDir.x, 0.4, ctx.inputDir.z);
    }

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(5);
    }
}
