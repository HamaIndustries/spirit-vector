package symbolics.division.spirit.vector.item;

import net.minecraft.item.Item;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;

public class DreamRuneItem extends Item {
    public DreamRuneItem(SpiritVectorAbility ability) {
        super(new Item.Settings().component(SpiritVectorAbility.COMPONENT, ability));
    }
}
