package symbolics.division.spirit.vector.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;
import symbolics.division.spirit.vector.logic.move.MovementType;

import java.util.List;

public class SpiritVectorItem extends ArmorItem {
    public SpiritVectorItem() {
        super(
                ArmorMaterials.DIAMOND,
                Type.BOOTS,
                new Settings().maxDamage(Type.BOOTS.getMaxDamage(33))
                        .component(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities())
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        var ab = stack.get(SpiritVectorHeldAbilities.COMPONENT);
        if (ab != null) {
            for (SpiritVectorAbility ability : ab.getAll()) {
                tooltip.add(Text.translatable(ability.getMovement().getID().toTranslationKey()));
            }
        }
    }
}
