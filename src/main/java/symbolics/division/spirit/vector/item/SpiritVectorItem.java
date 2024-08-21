package symbolics.division.spirit.vector.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;

import java.util.List;

public class SpiritVectorItem extends ArmorItem {
    public SpiritVectorItem() {
        super(
                ArmorMaterials.DIAMOND,
                Type.BOOTS,
                new Settings()
                        .component(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities())
                        .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(false))
                        .maxCount(1)
        );
    }

    @Override
    public Text getName(ItemStack stack) {
        return SFXPackItem.applySFXToText(stack, this, super.getName(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        var ab = stack.get(SpiritVectorHeldAbilities.COMPONENT);
        if (ab != null) {
            tooltip.add(Text.translatable("tooltip.spirit_vector.held_abilities").withColor(0x808080));
            tooltip.add(abilityText(ab, AbilitySlot.LEFT));
            tooltip.add(abilityText(ab, AbilitySlot.UP));
            tooltip.add(abilityText(ab, AbilitySlot.RIGHT));
        }
    }

    private MutableText abilityText(SpiritVectorHeldAbilities ab, AbilitySlot slot)  {
        // idk nobody like these
        return Text.literal("").withColor(0x808080)
                .append(Text.literal(slot.arrow).withColor(0xFFA500))
                .append(" [")
                .append(Text.keybind(slot.input.key).withColor(0xffffff))
                .append("] ")
                .append(Text.translatable(ab.get(slot).getMovement().getTranslationKey()).withColor(0xffffff));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
