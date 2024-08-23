package symbolics.division.spirit_vector.render;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import symbolics.division.spirit_vector.SpiritVectorMod;

public class SpiritVectorSkatesRenderer implements ArmorRenderer {

    private final SpiritVectorSkatesModel model;
    private static final Identifier texture = SpiritVectorMod.id("textures/armor/skates.png");

    public SpiritVectorSkatesRenderer() {
        this.model = new SpiritVectorSkatesModel(SpiritVectorSkatesModel.getTexturedModelData().createModel());
        model.setVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> baseModel) {
        VertexConsumer vtx = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), stack.hasGlint());

        baseModel.copyBipedStateTo(this.model);
        this.model.render(matrices, vtx, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);

    }
}
