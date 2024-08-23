package symbolics.division.spirit_vector.logic.ability;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit_vector.SpiritVectorMod;

import java.util.function.Consumer;

public record TeleportAbilityC2SPayload (Vec3d pos) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, TeleportAbilityC2SPayload> CODEC =
            CustomPayload.codecOf(
                    (p, b) -> b.writeVec3d(p.pos),
                    (b) -> new TeleportAbilityC2SPayload(b.readVec3d())
            );
    public static final CustomPayload.Id<TeleportAbilityC2SPayload> ID = SpiritVectorMod.payloadId("teleport_ability_c2s");

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void HANDLER (TeleportAbilityC2SPayload payload, ServerPlayNetworking.Context ctx) {
        Vec3d dest = payload.pos();
        ctx.player().teleport(
                ctx.player().getServerWorld(), dest.x, dest.y, dest.z, ctx.player().headYaw, ctx.player().getPitch()
        );
    }

    // sorry, again
    private static Consumer<Vec3d> clientRequester;
    public static void registerRequestCallback(Consumer<Vec3d> cb) {
        clientRequester = cb;
    }
    public static void requestTeleport(Vec3d pos) {
        if (clientRequester == null) {
            throw new RuntimeException("Requested teleport without registering request callback on client");
        }
        clientRequester.accept(pos);
    }
}
