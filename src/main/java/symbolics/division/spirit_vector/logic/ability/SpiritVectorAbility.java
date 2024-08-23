package symbolics.division.spirit_vector.logic.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.logic.move.MovementType;

import java.util.function.Function;

public interface SpiritVectorAbility {

    Identifier ID_NONE = SpiritVectorMod.id("none");
    SpiritVectorAbility NONE = new SpiritVectorAbility() {
        private static final String tk = SpiritVectorAbility.translationKeyOf(ID_NONE);
        @Override public MovementType getMovement() { return MovementType.NEUTRAL; }
        @Override public int cost() { return 0; }
        @Override public String abilityTranslationKey() { return tk; }
    };

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

    static String translationKeyOf(Identifier id) {
        return "spirit_vector_ability." + id.getNamespace() + "." + id.getPath();
    }

    /* v--- ACTUAL INTERFACE ---v */
    MovementType getMovement();
    int cost();
    String abilityTranslationKey();
}
