package symbolics.division.spirit.vector.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import symbolics.division.spirit.vector.SpiritVectorMod;

public record Registrar<T> (
        Identifier id,
        RegistryKey<Registry<T>> registryKey,
        Registry<T> registry,
        Codec<RegistryEntry<T>> entryCodec,
        PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryPacketCodec,
        ComponentType<RegistryEntry<T>> component
) {

    public static <T> Registrar<T> of(String name) { return of(SpiritVectorMod.id(name)); }
    public static <T> Registrar<T> of(Identifier id) { return of(id, null); }
    public static <T> Registrar<T> of(Identifier id, @Nullable PacketCodec<? super RegistryByteBuf, T> directPacketCodec) {
        RegistryKey<Registry<T>> key = RegistryKey.ofRegistry(id);
        SimpleRegistry<T> registry = FabricRegistryBuilder.createSimple(key).buildAndRegister();
        Codec<RegistryEntry<T>> entryCodec = RegistryFixedCodec.of(key);
        PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryPacketCodec = directPacketCodec == null ?
                PacketCodecs.registryEntry(key) :
                PacketCodecs.registryEntry(key, directPacketCodec);

        ComponentType<RegistryEntry<T>> component = Registry.register(Registries.DATA_COMPONENT_TYPE, id, ComponentType.<RegistryEntry<T>>builder()
                .codec(entryCodec)
                .packetCodec(entryPacketCodec)
                .build()
        );
        return new Registrar<T>(id, key, registry, entryCodec, entryPacketCodec, component);
    }

}
