package symbolics.division.spirit.vector.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import symbolics.division.spirit.vector.logic.SpiritVector;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @WrapWithCondition(
            method = "increaseTravelMotionStats",
            slice = @Slice(
                from = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isOnGround()Z"),
                to = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isFallFlying()Z")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V")
    )
    public boolean preventHungerLossWhileSliding(ServerPlayerEntity player, float exhaustion) {
        return !(SpiritVector.hasEquipped(player) && player.isSneaking());
    }

}
