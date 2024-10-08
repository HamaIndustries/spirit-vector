package symbolics.division.spirit_vector.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    MinecraftClient client;

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (client.getCameraEntity() instanceof PlayerEntity player
        && SpiritVector.hasEquipped(player)) {
            ci.cancel();
        }
    }
}
