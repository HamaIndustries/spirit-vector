package symbolics.division.spirit_vector.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import symbolics.division.spirit_vector.logic.ability.AbilitySlot;

import java.util.List;

public class SlotTemplateItem extends Item {
    public SlotTemplateItem(AbilitySlot slot) {
        super(new Item.Settings().component(AbilitySlot.COMPONENT, slot));
    }

    @Override
    public Text getName(ItemStack stack) {
        var slot = stack.get(AbilitySlot.COMPONENT);
        if (slot != null) {
            return Text.translatable(getTranslationKey(), slot.arrow);
        } else {
            return super.getName(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        AbilitySlot slot = stack.get(AbilitySlot.COMPONENT);
        if (slot != null) {
            tooltip.add(Text.translatable(
                    "tooltip.spirit_vector.slot_template_contents",
                    Text.keybind(slot.input.key).withColor(0xffffff)).withColor(0x808080)
            );
        }
    }
}
