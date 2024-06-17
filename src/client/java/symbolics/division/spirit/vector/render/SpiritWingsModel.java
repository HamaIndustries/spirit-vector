package symbolics.division.spirit.vector.render;

// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;

public class SpiritWingsModel<T extends LivingEntity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor

    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(SpiritVectorMod.MODID, "sv_wings_model"), "sv_wings");

    private final ModelPart root;

    public SpiritWingsModel(ModelPart root) {
        this.root = root.getChild("root");
    }

        public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        var root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 10.0F));
        var wing_l = root.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, -32).cuboid(0.0F, -16.0F, 0.0F, 0.0F, 32.0F, 32.0F, new Dilation(0)), ModelTransform.of(6.0F, -3.0F, -8.0F, 0.0F, 0.3491F, 0.0F));
        var wing_r = root.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, -32).cuboid(0.0F, -16.0F, 0.0F, 0.0F, 32.0F, 32.0F, new Dilation(0)), ModelTransform.of(-6.0F, -3.0F, -8.0F, 0.0F, -0.3491F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

//    public static TexturedModelData getTexturedModelData() {
//        ModelData modelData = new ModelData();
//        ModelPartData modelPartData = modelData.getRoot();
//        var root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
//        var wing_l = root.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, -32).cuboid(0.0F, -16.0F, 0.0F, 16.0F, 32.0F, 32.0F, new Dilation(0)), ModelTransform.of(6.0F, -3.0F, -8.0F, 0.0F, 0.3491F, 0.0F));
//        var wing_r = root.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, -32).cuboid(0.0F, -16.0F, 0.0F, 16.0F, 32.0F, 32.0F, new Dilation(0)), ModelTransform.of(-6.0F, -3.0F, -8.0F, 0.0F, -0.3491F, 0.0F));
//        return TexturedModelData.of(modelData, 32, 32);
//    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }

}