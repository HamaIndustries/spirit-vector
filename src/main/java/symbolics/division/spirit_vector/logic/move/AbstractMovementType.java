package symbolics.division.spirit_vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;
import symbolics.division.spirit_vector.logic.TravelMovementContext;

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
