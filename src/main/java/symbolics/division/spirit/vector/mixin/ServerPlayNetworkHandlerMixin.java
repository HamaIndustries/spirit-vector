package symbolics.division.spirit.vector.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyExpressionValue(
            method = "onPlayerMove",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isInTeleportationState()Z")
    )
    public boolean lieToServerThatWeAreTeleportingToPreventRubberbanding(boolean actuallyTeleporting) {
        return actuallyTeleporting || SpiritVector.hasEquipped(((ServerPlayNetworkHandler)(Object)this).player);
    }
}
