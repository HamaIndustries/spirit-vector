package symbolics.division.spirit.vector;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.item.SpiritVectorItem;

import static net.minecraft.registry.Registries.ITEM;
import static symbolics.division.spirit.vector.SpiritVectorMod.MODID;

public final class SpiritVectorItems {
    public static final Item SPIRIT_VECTOR = new SpiritVectorItem();

    private static void register(Item item, String id) {
        Registry.register(ITEM, Identifier.of(MODID, id), item);
    }

    public static void init() {
        register(SPIRIT_VECTOR, SpiritVectorItem.ID);
    }
}
