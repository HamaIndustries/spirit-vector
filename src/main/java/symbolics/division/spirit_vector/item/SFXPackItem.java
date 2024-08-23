package symbolics.division.spirit_vector.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import symbolics.division.spirit_vector.sfx.SFXPack;

public class SFXPackItem extends Item {
    public SFXPackItem(RegistryEntry<SFXPack<?>> entry) {
        super(new Item.Settings().component(SFXPack.COMPONENT, entry));
    }

    @Override
    public Text getName(ItemStack stack) {
        return applySFXToText(stack, this, super.getName(stack));
    }

    public static Text applySFXToText(ItemStack stack, Item item, Text text) {
        var sfx = stack.get(SFXPack.COMPONENT);
        if (sfx != null) {
            int color = sfx.value().color();
            text = text.copy().withColor(color);
        }
        return text;
    }
}
