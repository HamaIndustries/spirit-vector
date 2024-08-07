package symbolics.division.spirit.vector;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;

public class SpiritVectorTags {
    public static final class Items {
        private static TagKey<Item> of(String name) { return ofRegistry(RegistryKeys.ITEM, name); }

        public static final TagKey<Item> SFX_PACK_TEMPLATES = of("sfx_upgrade_templates");
        public static final TagKey<Item> SFX_PACK_ADDITIONS = of("sfx_upgrade_costs");
        public static final TagKey<Item> SLOT_UPGRADE_RUNES = of("slot_upgrade_runes");
        public static final TagKey<Item> ABILITY_UPGRADE_RUNES = of("ability_upgrade_runes");
    }

    public static final class Misc {
        public static final TagKey<SoundEvent> JUKEBOX_LOOPING = ofRegistry(RegistryKeys.SOUND_EVENT, "jukebox_looping");
    }

    private static <T> TagKey<T> ofRegistry(RegistryKey<? extends Registry<T>> key, String name) {
        return TagKey.of(key, SpiritVectorMod.id(name));
    }

    public static void init(){}
}
