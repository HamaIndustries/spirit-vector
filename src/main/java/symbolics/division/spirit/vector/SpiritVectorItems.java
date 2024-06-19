package symbolics.division.spirit.vector;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
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
    private static final List<Item> creativeTabItems = new ArrayList<>();

    public static final SpiritVectorItem SPIRIT_VECTOR = registerAndModel("spirit_vector", new SpiritVectorItem());
    public static final DreamRuneItem TELEPORT_RUNE = registerRuneAndModel("teleport", new TeleportAbility());
    public static final DreamRuneItem DASH_RUNE = registerRuneAndModel("dash", new DashAbility());

    static {
        for (SimpleSFX pack : SpiritVectorSFX.getSimpleSFX()) {
            registerAndModel("anima_core_" + pack.id.getPath(), pack.asItem());
        }
    }

    public static <T extends Item> T model(T item) {
        generatedItems.add(item);
        return item;
    }

    private static <T extends Item> T registerAndModel(String id, T item) {
        return addToTab(model(Registry.register(ITEM, Identifier.of(MODID, id), item)));
    }

    private static DreamRuneItem registerRuneAndModel(String id, SpiritVectorAbility ability) {
        return addToTab(model(SpiritVectorAbilitiesRegistry.registerRuneAndAbility(SpiritVectorMod.id(id), ability)));
    }

    public static List<Item> getGeneratedItems() {
        return generatedItems.stream().toList();
    }

    private static <T extends Item> T addToTab(T item) {
        creativeTabItems.add(item);
        return item;
    }

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SPIRIT_VECTOR))
            .displayName(Text.translatable("itemGroup.spirit_vector.creative_tab"))
            .entries((context, entries) -> { creativeTabItems.forEach(entries::add); })
            .build();

    static {
        Registry.register(Registries.ITEM_GROUP, SpiritVectorMod.id("item_group"), ITEM_GROUP);
    }

    public static void init () {}
}
