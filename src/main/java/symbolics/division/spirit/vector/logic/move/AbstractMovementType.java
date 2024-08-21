package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.skates.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;

public abstract class AbstractMovementType implements MovementType {
    protected Identifier id;
    public AbstractMovementType(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        return true;
    }
}
