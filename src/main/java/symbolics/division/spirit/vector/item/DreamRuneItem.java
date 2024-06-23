package symbolics.division.spirit.vector.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.text.WordUtils;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;

import java.util.List;

public class DreamRuneItem extends Item {
    public DreamRuneItem(SpiritVectorAbility ability) {
        super(new Item.Settings().component(SpiritVectorAbility.COMPONENT, ability));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        SpiritVectorAbility ability = stack.getOrDefault(SpiritVectorAbility.COMPONENT, SpiritVectorAbility.NONE);
        tooltip.add(Text.translatable("tooltip.spirit_vector.dream_rune_contents", Text.translatable(ability.getMovement().getTranslationKey()).withColor(0xfffffff)).withColor(0x808080));

        // this is so insanely cursed just look at the blame timestamp to know why
        String desc = Text.translatable("tooltip.desc." + ability.abilityTranslationKey()).getString();
        final int MAX_CHARS_PER_LINE = 25;
        String wrapped = WordUtils.wrap(desc, MAX_CHARS_PER_LINE,"%%", false);
        for (String s : wrapped.split("%%")) {
            tooltip.add(Text.literal(s).withColor(0xcccccc));
        }
    }
}
