package symbolics.division.spirit.vector.logic.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.MovementType;

import java.util.function.Function;

public interface SpiritVectorAbility {

    Identifier ID_NONE = SpiritVectorMod.id("none");
    SpiritVectorAbility NONE = new SpiritVectorAbility() {};

    Codec<SpiritVectorAbility> CODEC = Identifier.CODEC.flatXmap(
            nullableMapping("identifier to spirit vector ability", SpiritVectorAbilitiesRegistry.instance()::get),
            nullableMapping("spirit vector ability to identifier", SpiritVectorAbilitiesRegistry.instance()::getId)
    ).stable();

    PacketCodec<RegistryByteBuf, SpiritVectorAbility> PACKET_CODEC = PacketCodecs
            .registryValue(SpiritVectorAbilitiesRegistry.KEY);

    ComponentType<SpiritVectorAbility> COMPONENT = ComponentType.<SpiritVectorAbility>builder()
            .codec(CODEC)
            .packetCodec(PACKET_CODEC)
            .build();

    private static <From, To> Function<From, DataResult<To>> nullableMapping(String desc, Function<From, To> f) {
        return input -> {
            var result = f.apply(input);
            if (result == null) {
                return DataResult.error(() -> "failed to convert " + desc + ": " + input.toString());
            }
            return DataResult.success(result);
        };
    }

    /* v--- ACTUAL INTERFACE ---v */
    default MovementType getMovement() { return MovementType.NEUTRAL; }
}
