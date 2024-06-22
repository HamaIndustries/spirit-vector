package symbolics.division.spirit.vector.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import symbolics.division.spirit.vector.SpiritVectorMod;

public class SpiritWingsModel<T extends LivingEntity> extends EntityModel<T> {

    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(SpiritVectorMod.MODID, "sv_wings_model"), "sv_wings");

    protected final ModelPart root;
    protected final ModelPart leftWing;
    protected final ModelPart rightWing;

    public SpiritWingsModel(ModelPart base) {
        root = base.getChild(EntityModelPartNames.ROOT);
        leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
        rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData base = modelData.getRoot();
        ModelPartData root = base.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 10.0F));
        root.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, -32).cuboid(0.0F, -16.0F, 0.0F, 0.0F, 32.0F, 32.0F, new Dilation(0)), ModelTransform.of(6.0F, -3.0F, -8.0F, 0.0F, 0.3491F, 0.0F));
        root.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, -32).cuboid(0.0F, -16.0F, 0.0F, 0.0F, 32.0F, 32.0F, new Dilation(0)), ModelTransform.of(-6.0F, -3.0F, -8.0F, 0.0F, -0.3491F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float tickDelta) {
        float swing = entity.isInPose(EntityPose.CROUCHING) ? 0 : limbSwing;
        float angle = (float)Math.cos((ageInTicks + tickDelta) / 10 + swing / 2) * (0.2f + 0.1f * limbSwingAmount) - 0.3f;
        this.rightWing.yaw = angle;
        this.leftWing.yaw = -angle;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }
    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, 0);
    }
}