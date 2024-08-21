package symbolics.division.spirit.vector.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import symbolics.division.spirit.vector.SpiritVectorItems;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.skates.SpiritVector;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;

public class SpiritGaugeHUD {

    private static final int WIDTH = 9;
    private static final int HEIGHT = 85;
    private static final int VALUE_WIDTH = 3;
    private static final int VALUE_HEIGHT = 77;
    private static final int VALUE_OFFSET = 4;
    private static final int SLOT_WIDTH = 11;
    private static final int SLOT_HEIGHT = 11;
    private static final int SLOT_OFFSET = 5;
    private static final Identifier BAR_BG = SpiritVectorMod.id("textures/gui/momentum_meter_bg.png");
    private static final Identifier BAR_FG = SpiritVectorMod.id("textures/gui/momentum_meter_fg.png");
    private static final Identifier BAR_VALUE = SpiritVectorMod.id("textures/gui/momentum_meter_value.png");
    private static final Identifier SLOT_LEFT = SpiritVectorMod.id("textures/gui/slot_indicator_left.png");
    private static final Identifier SLOT_UP = SpiritVectorMod.id("textures/gui/slot_indicator_up.png");
    private static final Identifier SLOT_RIGHT = SpiritVectorMod.id("textures/gui/slot_indicator_right.png");

    private static final SpiritGaugeHUD hud = new SpiritGaugeHUD();

    public static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (    player.isAlive()
                && shouldRenderGauge(player)
                && player instanceof ISpiritVectorUser user) {
            var sv = user.spiritVector();
            if (sv == null) return;
            hud.render(drawContext, tickCounter, sv);
        }
    }

    private static boolean shouldRenderGauge(ClientPlayerEntity player) {
        return player.getStackInHand(Hand.MAIN_HAND).isOf(SpiritVectorItems.MOMENTUM_GAUGE)
                || player.getStackInHand(Hand.OFF_HAND).isOf(SpiritVectorItems.MOMENTUM_GAUGE);
    }

    private int trackedMomentum;

    public void render(DrawContext drawContext, RenderTickCounter tickCounter, SpiritVector sv) {

        int momentumDiff = sv.getMomentum() - trackedMomentum;
        if (Math.abs(momentumDiff) <= 1) {
            trackedMomentum = sv.getMomentum();
        } else {
            int delta = (int)((float)momentumDiff / 20f);
            trackedMomentum += delta != 0 ? delta : Math.signum(momentumDiff);
        }

        int visibleHeight = (int)(VALUE_HEIGHT * ((float)trackedMomentum / SpiritVector.MAX_MOMENTUM));
        int x = 5;
        int y = (int)(drawContext.getScaledWindowHeight() * 0.05);

        drawContext.drawTexture(
                BAR_BG, x, y,0, 0,WIDTH, HEIGHT, WIDTH, HEIGHT
        );

        int color = sv.getSFX().color();
        final float red = ((color >>> 16) & 0xFF) / 255f;
        final float green = ((color >>> 8) & 0xFF) / 255f;
        final float blue = (color & 0xFF) / 255f;

        this.drawTexture(
                drawContext.getMatrices(),
                BAR_VALUE,
                x + 3, y + (HEIGHT + 1 - VALUE_OFFSET - visibleHeight),
                0, VALUE_HEIGHT - visibleHeight,
                VALUE_WIDTH, visibleHeight ,
                VALUE_WIDTH, VALUE_HEIGHT,
                red, green, blue, 1
        );

        drawContext.drawTexture(
                BAR_FG, x, y,0, 0,WIDTH, HEIGHT, WIDTH, HEIGHT
        );

        final int baseY = y + VALUE_OFFSET;
        this.drawSlot(drawContext, sv, SLOT_LEFT, AbilitySlot.LEFT, x+VALUE_OFFSET+2, baseY, VALUE_HEIGHT, red, green, blue);
        this.drawSlot(drawContext, sv, SLOT_UP, AbilitySlot.UP, x+VALUE_OFFSET+2, baseY, VALUE_HEIGHT, red, green, blue);
        this.drawSlot(drawContext, sv, SLOT_RIGHT, AbilitySlot.RIGHT, x+VALUE_OFFSET+2, baseY, VALUE_HEIGHT, red, green, blue);
    }

    private void drawSlot(DrawContext drawContext, SpiritVector sv, Identifier slotTexture, AbilitySlot slot, int x, int baseY, int h, float r, float g, float b) {
        // todo: please improve this
        var ability = sv.heldAbilities().get(slot);
        float cost = (float)ability.cost() / SpiritVector.MAX_MOMENTUM;
        if (cost <= 0) return;
        int offset = h - (int)(h * cost);

        this.drawTexture(
                drawContext.getMatrices(),
                slotTexture,
                x + SLOT_OFFSET, baseY + offset - SLOT_OFFSET,
                0, 0,
                SLOT_WIDTH, SLOT_HEIGHT,
                SLOT_WIDTH, SLOT_HEIGHT,
                r, g, b,1
        );
    }

    private void drawTexture(MatrixStack matrices, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, float r, float g, float b, float a) {
        int x2 = x + width;
        int y2 = y + height;
        this.drawTexturedQuad(matrices, texture, x, x2, y, y2, 0, (u + 0.0F) / (float)textureWidth, (u + (float)width) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)height) / (float)textureHeight, r, g, b, a);
    }

    private void drawTexturedQuad(MatrixStack matrices, Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, float red, float green, float blue, float alpha) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, (float)z).texture(u1, v1).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, (float)z).texture(u1, v2).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, (float)z).texture(u2, v2).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, (float)z).texture(u2, v1).color(red, green, blue, alpha);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
}
