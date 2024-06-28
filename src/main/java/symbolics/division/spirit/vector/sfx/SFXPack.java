package symbolics.division.spirit.vector.sfx;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorRegistration;

public interface SFXPack<T extends ParticleEffect> {
    Identifier wingsTexture();
    ParticleType<T> particleType();
    T particleEffect();
    default int color() { return 0xffffff; }

    static SFXPack<?> getFromStack(ItemStack stack) {
        return stack.getComponents().getOrDefault(COMPONENT, SFXRegistry.defaultEntry()).value();
    }

    PacketCodec<RegistryByteBuf, SFXPack<?>> PACKET_CODEC = PacketCodecs.registryValue(SFXRegistry.KEY);

    // todo: replace unit with factory/real codec for making datapack-driven sfx packs
    Codec<RegistryEntry<SFXPack<?>>> ENTRY_CODEC = RegistryFixedCodec.of(SFXRegistry.KEY);
//    Codec<RegistryEntry<SFXPack<?>>> ENTRY_CODEC = RegistryElementCodec.of(SFXRegistry.KEY, Codec.unit(SpiritVectorSFX.getDefault()));
    PacketCodec<RegistryByteBuf, RegistryEntry<SFXPack<?>>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(SFXRegistry.KEY, PACKET_CODEC);

    ComponentType<RegistryEntry<SFXPack<?>>> COMPONENT = SpiritVectorRegistration.registerComponent(
            "sfx_pack", builder -> builder.codec(ENTRY_CODEC).packetCodec(ENTRY_PACKET_CODEC).cache()
    ) ;
}
