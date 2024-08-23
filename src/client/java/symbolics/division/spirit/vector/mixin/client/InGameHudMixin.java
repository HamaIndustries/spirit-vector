package symbolics.division.spirit.vector.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.spirit.vector.render.SpiritVectorHUD;

@Mixin(InGameHud.class)
public class InGameHudMixin {
//    @Inject(
//            method = "renderStatusBars", at = @At("HEAD")
//    )
//    public void appendPoiseStatusBar(DrawContext ctx, CallbackInfo ci) {
//
//    }

    @WrapOperation(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V")
    )
    public void moveBubbleRenderUp(DrawContext ctx, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
        if (SpiritVectorHUD.numFeathers() > 0) {
            original.call(ctx,texture, x, y-10, width, height);
        } else {
            original.call(ctx,texture, x, y, width, height);
        }
    }

    @WrapOperation(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V")
    )
    public void renderPoiseAboveFood(InGameHud self, DrawContext ctx, PlayerEntity player, int top, int right, Operation<Void> operation) {
        operation.call(self, ctx, player, top, right);
        SpiritVectorHUD.renderPoise(ctx, top-10, right);
    }
}
