package symbolics.division.spirit.vector.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Hand;
import symbolics.division.spirit.vector.SpiritVectorItems;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;

public class SpiritGaugeHUD {
    public static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player.getStackInHand(Hand.MAIN_HAND).isOf(SpiritVectorItems.MOMENTUM_GAUGE)
                && player instanceof ISpiritVectorUser user) {
            var sv = user.spiritVector();
            if (sv == null) return;
            drawContext.drawCenteredTextWithShadow(
                    client.textRenderer,
                    "Momentum: " + sv.getMomentum() + " / " + SpiritVector.MAX_MOMENTUM,
                    drawContext.getScaledWindowWidth() / 2,
                    drawContext.getScaledWindowHeight() / 2,
                    0xffffff
            );
        }
    }
}
