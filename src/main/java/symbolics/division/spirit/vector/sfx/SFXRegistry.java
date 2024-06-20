package symbolics.division.spirit.vector.sfx;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import symbolics.division.spirit.vector.SpiritVectorMod;

public class SFXRegistry {
    public static final RegistryKey<Registry<SFXPack<?>>> KEY = RegistryKey.ofRegistry(SpiritVectorMod.id("sfx"));
    public static final Registry<SFXPack<?>> INSTANCE = FabricRegistryBuilder
            .from(new SimpleRegistry<>(KEY, Lifecycle.stable(), false))
            .buildAndRegister();

    public static RegistryEntry<SFXPack<?>> defaultEntry() {
        return RegistryEntry.of(SpiritVectorSFX.getDefault());
    }

}
