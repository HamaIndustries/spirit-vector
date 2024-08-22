package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.ability.WaterRunAbility;
import symbolics.division.spirit.vector.logic.input.Input;

public class JumpingMovement extends NeutralMovement {

    public JumpingMovement(Identifier id) { super(id); }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        if ((sv.user.groundCollision || WaterRunAbility.isWaterRunning(sv))
                && sv.inputManager().consume(Input.JUMP)) {
            // reset other buttons for abilities
            sv.inputManager().consume(Input.CROUCH);
            sv.inputManager().consume(Input.SPRINT);
            return true;
        }
        return false;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        float f = sv.user.getJumpVelocity() * 1.2f;
        if (f <= 0.00001) return;
        sv.user.addVelocity(0, f, 0);
        sv.user.velocityDirty = true;
        super.travel(sv, ctx);
    }

    @Override
    public void updateValues(SpiritVector sv) {}
}
