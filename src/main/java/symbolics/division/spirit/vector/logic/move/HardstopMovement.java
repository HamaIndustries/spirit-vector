package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.input.Input;

public class HardstopMovement extends NeutralMovement {
    public HardstopMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        // continue to brake until released on ground,
        // only apply once if on wall
        return false;
//        if (       (sv.user.isOnGround() || (sv.getMoveState() == MovementType.WALL_RUSH && sv.user.getVelocity().withAxis(Direction.Axis.Y, 0).lengthSquared() > 0.01))
//                && sv.inputManager().consume(Input.SPRINT)
//        ) {
////            ;
//            return true;
//        }
//        return false;
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        return !(sv.user.isOnGround() && sv.inputManager().rawInput(Input.SPRINT));
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        sv.user.setVelocity(0, 0, 0);
        sv.user.speed = 0;
        sv.user.limbAnimator.updateLimbs(0, 1); // strange effect when movement zero'd
//        ctx.ci().cancel();
        MovementType.NEUTRAL.travel(sv, new TravelMovementContext(Vec3d.ZERO, ctx.ci(), ctx.inputDir()));
    }
}
