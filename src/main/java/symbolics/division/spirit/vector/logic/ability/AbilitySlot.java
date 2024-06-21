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

    LEFT("left", Input.CROUCH), UP("up", Input.JUMP), RIGHT("right", Input.SPRINT);

    public final Input input;
    public final String name;

    AbilitySlot(String name, Input input) {
        this.input = input; this.name = name;
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
