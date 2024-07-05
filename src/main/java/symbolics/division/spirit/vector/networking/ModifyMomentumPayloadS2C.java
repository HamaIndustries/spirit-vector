package symbolics.division.spirit.vector.networking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;

public record ModifyMomentumPayloadS2C (int amount, boolean burst) implements CustomPayload {
    public static final CustomPayload.Id<ModifyMomentumPayloadS2C> ID = SpiritVectorMod.payloadId("modify_momentum_s2c");
    public static final PacketCodec<PacketByteBuf, ModifyMomentumPayloadS2C> CODEC =
            CustomPayload.codecOf(
                    (p, b) -> b.writeInt(p.amount).writeBoolean(p.burst),
                    (b) -> new ModifyMomentumPayloadS2C(b.readInt(), b.readBoolean())
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void HANDLER (ModifyMomentumPayloadS2C payload, PlayerEntity player) {
        if (player instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                if (payload.burst && sv.modifyMomentumWithCooldown(payload.amount, 60)) {
                    sv.effectsManager().spawnRing(player.getPos(), new Vec3d(0, 1, 0));
                }
            });
        }
    }
}
