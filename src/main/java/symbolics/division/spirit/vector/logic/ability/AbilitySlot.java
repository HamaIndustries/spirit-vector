package symbolics.division.spirit.vector.logic.ability;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import symbolics.division.spirit.vector.SpiritVectorRegistration;
import symbolics.division.spirit.vector.logic.input.Input;

public enum AbilitySlot implements StringIdentifiable {

    LEFT("left", Input.CROUCH, "\uD83E\uDC1C"), UP("up", Input.JUMP, "\uD83E\uDC1D"), RIGHT("right", Input.SPRINT, "\uD83E\uDC1E");

    public final Input input;
    public final String name;
    public final String arrow;

    AbilitySlot(String name, Input input, String arrow) {
        this.input = input; this.name = name; this.arrow = arrow;
    }

    @Override
    public String asString() {
        return name;
    }

    public static final Codec<AbilitySlot> CODEC = StringIdentifiable.createCodec(AbilitySlot::values);
    public static final PacketCodec<ByteBuf, AbilitySlot> PACKET_CODEC = PacketCodecs.codec(CODEC);
    public static final ComponentType<AbilitySlot> COMPONENT = SpiritVectorRegistration.registerComponent("ability_slot",
            builder -> builder.codec(CODEC).packetCodec(PACKET_CODEC).cache()
    );
}
