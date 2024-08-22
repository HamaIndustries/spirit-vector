package symbolics.division.spirit.vector.compat;

import dev.kleinbox.dancerizer.common.api.PlayerAnimationCallback;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.networking.ModifyMomentumPayloadS2C;

public class DancerizerCompat implements ModCompatibility {
    private static final int TAUNT_MOMENTUM_GAIN = SpiritVector.MAX_MOMENTUM / 5;

    @Override
    public void initialize(String modid, boolean inDev) {
        SpiritVectorMod.LOGGER.debug("Dancerizer setup");
        PlayerAnimationCallback.EVENT.register(DancerizerCompat::momentumGainCallback);
    }


    private static ActionResult momentumGainCallback(Object source, Object IGNORED_ANIM_TYPE) {
        if (source instanceof ServerPlayerEntity player && player.speed > 2 && !player.isOnGround()) {
            ServerPlayNetworking.send(player, new ModifyMomentumPayloadS2C(TAUNT_MOMENTUM_GAIN, true));
        }
        return ActionResult.PASS;
    }
}
