package symbolics.division.spirit.vector.logic.ability;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import symbolics.division.spirit.vector.SpiritVectorMod;

import java.util.function.Consumer;

public record SlamPacketC2S(float power) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, SlamPacketC2S> CODEC =
            CustomPayload.codecOf(
                    (p, b) -> b.writeFloat(p.power),
                    (b) -> new SlamPacketC2S(b.readFloat())
            );
    public static final CustomPayload.Id<SlamPacketC2S> ID = SpiritVectorMod.payloadId("slam_attack_c2s");

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void HANDLER (SlamPacketC2S payload, ServerPlayNetworking.Context ctx) {
        GroundPoundAbility.doSlamEffect(ctx.player(), payload.power);
    }

    private static Consumer<Float> clientRequester;
    public static void registerRequestCallback(Consumer<Float> cb) {
        clientRequester = cb;
    }
    public static void requestSlam(float power) {
        if (clientRequester == null) {
            throw new RuntimeException("Requested slam without registering request callback on client");
        }
        clientRequester.accept(power);
    }
}