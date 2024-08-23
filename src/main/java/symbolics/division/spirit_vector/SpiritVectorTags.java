package symbolics.division.spirit_vector;

import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SpiritVectorTags {
    public static final class Items {
        private static TagKey<Item> of(String name) { return ofRegistry(RegistryKeys.ITEM, name); }

        public static final TagKey<Item> SFX_PACK_TEMPLATES = of("sfx_upgrade_templates");
        public static final TagKey<Item> SFX_PACK_ADDITIONS = of("sfx_upgrade_materials");
        public static final TagKey<Item> SLOT_UPGRADE_RUNES = of("slot_upgrade_runes");
        public static final TagKey<Item> ABILITY_UPGRADE_RUNES = of("ability_upgrade_runes");

        public static final TagKey<Item> SPIRIT_VECTOR_CRAFTING_MATERIALS = of("spirit_vector_crafting_materials");
    }

    public static final class Misc {
        public static final TagKey<SoundEvent> JUKEBOX_LOOPING = ofRegistry(RegistryKeys.SOUND_EVENT, "jukebox_looping");
    }

    private static <T> TagKey<T> ofRegistry(RegistryKey<? extends Registry<T>> key, String name) {
        return TagKey.of(key, SpiritVectorMod.id(name));
    }

    public static <T> TagKey<T> common(RegistryKey<Registry<T>> key, String id) {
        return TagKey.of(key, Identifier.of(TagUtil.C_TAG_NAMESPACE, id));
    }

    public static void init(){}
}
