package symbolics.division.spirit.vector.sfx;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbilitiesRegistry;

public class SFXRegistry {
    public static final RegistryKey<Registry<SFXPack<?>>> KEY = RegistryKey.ofRegistry(SpiritVectorMod.id("sfx"));
    public static final Registry<SFXPack<?>> INSTANCE = FabricRegistryBuilder
            .from(new SimpleRegistry<>(KEY, Lifecycle.stable(), false))
            .buildAndRegister();

    public static final PacketCodec<RegistryByteBuf, SFXPack<?>> PACKET_CODEC = PacketCodecs.registryValue(KEY);

    public static void register(SFXPack<?> pack) {
        Registry.register(INSTANCE, pack.id(), pack);
    }
}
