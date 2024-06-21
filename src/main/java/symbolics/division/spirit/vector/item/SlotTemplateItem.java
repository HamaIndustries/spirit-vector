package symbolics.division.spirit.vector.item;

import net.minecraft.item.Item;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;

public class SlotTemplateItem extends Item {
    public SlotTemplateItem(AbilitySlot slot) {
        super(new Item.Settings().component(AbilitySlot.COMPONENT, slot));
    }
}
