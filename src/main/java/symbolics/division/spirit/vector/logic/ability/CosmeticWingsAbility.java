package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.state.WingsEffectState;

public class CosmeticWingsAbility extends AbstractSpiritVectorAbility {

    public CosmeticWingsAbility(Identifier id) {
        super(id, Integer.MAX_VALUE);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return false;
    }

    @Override
    public void configure(SpiritVector sv) {
        // enable it forever!!!
        sv.stateManager().enableState(WingsEffectState.ID);
    }

    @Override public void travel(SpiritVector sv, TravelMovementContext ctx) {}
}
