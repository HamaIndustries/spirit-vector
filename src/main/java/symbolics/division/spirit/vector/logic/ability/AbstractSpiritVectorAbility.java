package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.move.AbstractMovementType;
import symbolics.division.spirit.vector.logic.move.MovementType;

public abstract class AbstractSpiritVectorAbility extends AbstractMovementType implements SpiritVectorAbility {

    protected int abilityCost;
    protected String translationString;

    protected AbstractSpiritVectorAbility(Identifier id, int cost) {
        super(id);
        this.abilityCost = cost;
        this.translationString = SpiritVectorAbility.translationKeyOf(id);
    }

    @Override
    public MovementType getMovement() {
        return this;
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        // the condition of being in air and button pressed is handled by the sv
        // so we normally expect to be able to fire this
        return true;
    }

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(-abilityCost);
    }

    @Override
    public int cost() {
        return abilityCost;
    }

    @Override
    public String abilityTranslationKey() {
        return translationString;
    }
}
