package symbolics.division.spirit.vector;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import symbolics.division.spirit.vector.item.DreamRuneItem;
import symbolics.division.spirit.vector.item.SlotTemplateItem;
import symbolics.division.spirit.vector.item.SpiritVectorItem;
import symbolics.division.spirit.vector.logic.ability.*;
import symbolics.division.spirit.vector.sfx.SimpleSFX;
import symbolics.division.spirit.vector.sfx.SpiritVectorSFX;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.registry.Registries.ITEM;
import static symbolics.division.spirit.vector.SpiritVectorMod.MODID;

public final class SpiritVectorItems {

    private static final List<Item> generatedItems = new ArrayList<>();
    private static final List<Item> creativeTabItems = new ArrayList<>();
    private static final List<DreamRuneItem> dreamRuneItems = new ArrayList<>();
    private static final List<Item> sfxUpgradeItems = new ArrayList<>();

    public static final SpiritVectorItem SPIRIT_VECTOR = registerAndModel("spirit_vector", new SpiritVectorItem());

    public static final DreamRuneItem NULL_RUNE = SpiritVectorAbilitiesRegistry.registerNullItem();
    public static final DreamRuneItem TELEPORT_RUNE = registerRuneAndModel("teleport", TeleportAbility::new);
    public static final DreamRuneItem DASH_RUNE = registerRuneAndModel("dash", DashAbility::new);
    public static final DreamRuneItem SLAM_RUNE = registerRuneAndModel("slam", GroundPoundAbility::new);
    public static final DreamRuneItem SOAR_RUNE = registerRuneAndModel("soar", BulletJumpAbility::new);
    public static final DreamRuneItem LEAP_RUNE = registerRuneAndModel("leap", TransgenderAbility::new);
    public static final DreamRuneItem POWER_SLIDE_RUNE = registerRuneAndModel("power_slide", PowerSlideAbility::new);
    public static final DreamRuneItem COSMETIC_WINGS_RUNE = registerRuneAndModel("cosmetic_wings", CosmeticWingsAbility::new);

    public static final SlotTemplateItem LEFT_SLOT_TEMPLATE = registerAndModel("burst_rune_left", new SlotTemplateItem(AbilitySlot.LEFT));
    public static final SlotTemplateItem UP_SLOT_TEMPLATE = registerAndModel("burst_rune_up", new SlotTemplateItem(AbilitySlot.UP));
    public static final SlotTemplateItem RIGHT_SLOT_TEMPLATE = registerAndModel("burst_rune_right", new SlotTemplateItem(AbilitySlot.RIGHT));

//    public static final Item TAKE_BREAK_CASSETTE = registerCassette(SpiritVectorSounds.TAKE_BREAK_SONG);
//    public static final Item SHOW_DONE_SONG = registerCassette(SpiritVectorSounds.SHOW_DONE_SONG);

    static {
        for (SimpleSFX pack : SpiritVectorSFX.getSimpleSFX()) {
            sfxUpgradeItems.add(registerAndModel("anima_core_" + pack.id.getPath(), pack.asItem()));
        }
    }

    public static <T extends Item> T model(T item) {
        generatedItems.add(item);
        return item;
    }

    private static <T extends Item> T registerAndModel(String id, T item) {
        return addToTab(model(Registry.register(ITEM, Identifier.of(MODID, id), item)));
    }

    private static <T extends Item> T registerAndModel(Identifier id, T item) {
        return addToTab(model(Registry.register(ITEM, id, item)));
    }

    private static DreamRuneItem registerRuneAndModel(String id, Function<Identifier, SpiritVectorAbility> abilityProvider) {
        Identifier ident = SpiritVectorMod.id(id);
        DreamRuneItem item = addToTab(model(SpiritVectorAbilitiesRegistry.registerRuneAndAbility(ident, abilityProvider.apply(ident))));
        dreamRuneItems.add(item);
        return item;
    }

    private static Item registerCassette(SoundEvent sound) {
        var id = sound.getId().withPath(p -> p.replace('.', '_'));
        var key = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, sound.getId().withPath(p -> p.substring(p.indexOf('.')+1)));
        return registerAndModel(id, new Item(new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(key)));
    }

    public static List<Item> getGeneratedItems() {
        return generatedItems.stream().toList();
    }

    public static List<DreamRuneItem> getDreamRunes() {
        return dreamRuneItems.stream().toList();
    }

    public static List<Item> getSfxUpgradeItems() {
        return sfxUpgradeItems.stream().toList();
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
