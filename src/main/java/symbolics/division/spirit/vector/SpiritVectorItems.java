package symbolics.division.spirit.vector;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.item.DreamRuneItem;
import symbolics.division.spirit.vector.item.SpiritVectorItem;
import symbolics.division.spirit.vector.logic.ability.DashAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbilitiesRegistry;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit.vector.logic.ability.TeleportAbility;

import static net.minecraft.registry.Registries.ITEM;
import static symbolics.division.spirit.vector.SpiritVectorMod.MODID;

public final class SpiritVectorItems {
    public static final Item SPIRIT_VECTOR = new SpiritVectorItem();

    private static void register(String id, Item item) {
        Registry.register(ITEM, Identifier.of(MODID, id), item);
    }

    private static void registerRune(String id, SpiritVectorAbility ability) {
        SpiritVectorAbilitiesRegistry.registerRuneAndAbility(SpiritVectorMod.id(id), ability);
    }

    public static void init() {
        register(SpiritVectorItem.ID, SPIRIT_VECTOR);
        registerRune("teleport", new TeleportAbility());
        registerRune("dash", new DashAbility());
    }
}
