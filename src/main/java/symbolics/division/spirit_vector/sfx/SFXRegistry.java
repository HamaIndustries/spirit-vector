package symbolics.division.spirit_vector.sfx;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import symbolics.division.spirit_vector.SpiritVectorMod;

public class SFXRegistry {
    public static final RegistryKey<Registry<SFXPack<?>>> KEY = RegistryKey.ofRegistry(SpiritVectorMod.id("sfx"));
    public static final Registry<SFXPack<?>> INSTANCE = FabricRegistryBuilder
            .from(new SimpleRegistry<>(KEY, Lifecycle.stable(), false))
            .buildAndRegister();

    public static RegistryEntry<SFXPack<?>> defaultEntry() {
        return entryOf(SpiritVectorSFX.getDefault());
    }

    public static RegistryEntry<SFXPack<?>> entryOf(SFXPack<?> pack) {
        var key = INSTANCE.getKey(pack);
        return key.or(() -> {
            SpiritVectorMod.LOGGER.error("Trying to get a key for non-registered SFX pack " + pack + ", this should not happen! Message the mod author.\nSupplying default instead.");
            return INSTANCE.getKey(SpiritVectorSFX.getDefault());
        }).flatMap(INSTANCE::getEntry)
        .orElseThrow();
    }
}
