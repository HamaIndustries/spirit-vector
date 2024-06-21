package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.mixin.LivingEntityAccessor;

public class JumpingMovement extends GroundMovement {

    public JumpingMovement(Identifier id) { super(id); }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        if (sv.user.isOnGround() && sv.inputManager().consume(Input.JUMP)) {
            // reset other buttons for abilities
            sv.inputManager().consume(Input.CROUCH);
            sv.inputManager().consume(Input.SPRINT);
            return true;
        }
        return false;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        float f = ((LivingEntityAccessor)sv.user).callGetJumpVelocity() * 1.2f;
        if (f <= 0.00001) return;
        sv.user.addVelocity(0, f, 0);
        sv.user.velocityDirty = true;
        super.travel(sv, ctx);
    }

    @Override
    public void updateValues(SpiritVector sv) {}
}
