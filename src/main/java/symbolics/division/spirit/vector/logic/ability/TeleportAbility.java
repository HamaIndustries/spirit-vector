package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.SpiritVector;

public class TeleportAbility extends AbstractSpiritVectorAbility {
    public TeleportAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 2);
    }
}
