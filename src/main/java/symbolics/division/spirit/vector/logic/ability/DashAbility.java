package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;

public class DashAbility extends AbstractSpiritVectorAbility {
    public DashAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 10);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return true;
    }

    @Override
    public void updateValues(SpiritVector sv) {

    }

}
