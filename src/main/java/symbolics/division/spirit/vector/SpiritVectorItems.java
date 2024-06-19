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
import symbolics.division.spirit.vector.sfx.SimpleSFX;
import symbolics.division.spirit.vector.sfx.SpiritVectorSFX;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.registry.Registries.ITEM;
import static symbolics.division.spirit.vector.SpiritVectorMod.MODID;

public final class SpiritVectorItems {
    private static final List<Item> generatedItems = new ArrayList<>();

    public static final SpiritVectorItem SPIRIT_VECTOR = registerAndModel("spirit_vector", new SpiritVectorItem());
    public static final DreamRuneItem TELEPORT_RUNE = registerRuneAndModel("teleport", new TeleportAbility());
    public static final DreamRuneItem DASH_RUNE = registerRuneAndModel("dash", new DashAbility());

    public static void init() {
        for (SimpleSFX pack : SpiritVectorSFX.getSimpleSFX()) {
            registerAndModel("anima_core_" + pack.id.getPath(), pack.asItem());
        }
    }

    public static <T extends Item> T model(T item) {
        generatedItems.add(item);
        return item;
    }

    private static <T extends Item> T registerAndModel(String id, T item) {
        return model(Registry.register(ITEM, Identifier.of(MODID, id), item));
    }

    private static DreamRuneItem registerRuneAndModel(String id, SpiritVectorAbility ability) {
        return model(SpiritVectorAbilitiesRegistry.registerRuneAndAbility(SpiritVectorMod.id(id), ability));
    }

    public static List<Item> getGeneratedItems() {
        return generatedItems.stream().toList();
    }

}
