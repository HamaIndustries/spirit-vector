package symbolics.division.spirit.vector.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.skates.SpiritVector;

public record SVEntityState (boolean wingsVisible)  {
    public static final Codec<SVEntityState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("wingsVisible").forGetter(SVEntityState::wingsVisible)
            ).apply(instance, SVEntityState::new)
    );
    public static final PacketCodec<ByteBuf, SVEntityState> PACKET_CODEC = PacketCodecs.codec(CODEC);
    public static final AttachmentType<SVEntityState> ATTACHMENT = AttachmentRegistry.createPersistent(SpiritVectorMod.id("entity_sv_state"), CODEC);

    public static record Payload (int targetId, SVEntityState state) implements CustomPayload {
        public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(SpiritVectorMod.id("entity_sv_state"));
        public static final PacketCodec<ByteBuf, Payload> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, Payload::targetId,
                SVEntityState.PACKET_CODEC, Payload::state,
                Payload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void handleStateSyncC2S(Payload payload, ServerPlayNetworking.Context ctx) {
        handleStateSync(payload, ctx.player().getServerWorld());
        ctx.server().getPlayerManager().getPlayerList().stream()
                .filter(p -> p != ctx.player())
                .forEach(p -> ServerPlayNetworking.send(p, payload));
    }

    public static void handleStateSync(Payload payload, World world) {
        Entity e = world.getEntityById(payload.targetId);
        if (e instanceof LivingEntity entity) {
            entity.setAttached(ATTACHMENT, payload.state);
        }
    }

    public static void updatePlayerStatesOnSpawn(ServerPlayerEntity player) {
        for (ServerPlayerEntity other : player.getServerWorld().getPlayers(SpiritVector::hasEquipped)) {
            if (other == player) return;
            var state = other.getAttachedOrElse(ATTACHMENT, defaultState());
            ServerPlayNetworking.send(player, new Payload(other.getId(), state));
        }
    }

    public static SVEntityState defaultState() {
        return new SVEntityState(false);
    }
}
