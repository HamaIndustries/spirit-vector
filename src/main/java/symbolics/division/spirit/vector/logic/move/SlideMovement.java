package symbolics.division.spirit.vector.logic.move;

import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.MovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;

public class SlideMovement extends BaseMovement  {
    public SlideMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, MovementContext ctx) {
        return sv.user.isSneaking() && !ctx.jumping && sv.user.isOnGround();
    }

    @Override
    public void travel(SpiritVector sv, MovementContext ctx) {
        sv.user.setVelocity(sv.user.getVelocity().x, -0.001, sv.user.getVelocity().z);
        Vec3d vel = sv.user.getVelocity();

        // check ledge climb
//        var pos = sv.user.getBlockPos();
//        Direction[] dirs = {
//                ctx.inputDir.getComponentAlongAxis(Direction.Axis.Z) < 0 ? Direction.NORTH : Direction.SOUTH,
//                ctx.inputDir.getComponentAlongAxis(Direction.Axis.X) < 0 ? Direction.WEST : Direction.EAST
//        };
//
//        var world = sv.user.getWorld();
//        for (Direction dir : dirs) {
//            BlockPos wallPos = pos.offset(dir);
//            BlockState wallState = world.getBlockState(wallPos);
//            if (sv.user.age % 20 == 0) {
//                System.out.println("wwwweeeeeeeeeeeeeeee" + sv.user.collidedSoftly);
//            }
//            if (sv.user.collidesWithStateAtPos(wallPos, wallState) && sv.user.doesNotCollide(vel.x, 1, vel.y)) {
//
//
//                sv.user.move(MovementType.SELF, new Vec3d(0, 1, 0));
//                break;
//            }
//
//        }

        sv.user.move(net.minecraft.entity.MovementType.SELF, sv.user.getVelocity());

        ctx.ci.cancel();
    }


    @Override
    public void updateValues(SpiritVector sv) {}
}
