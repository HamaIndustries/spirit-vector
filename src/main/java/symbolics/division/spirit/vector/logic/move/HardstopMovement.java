package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.input.Input;

public class HardstopMovement extends GroundMovement {
    public HardstopMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        // continue to brake until released
        if (sv.user.isOnGround() && sv.inputManager().rawInput(Input.SPRINT)) {
            sv.inputManager().consume(Input.SPRINT);
            return true;
        }
        return false;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        sv.user.setVelocity(0, 0, 0);
        ctx.ci().cancel();
    }
}
