package symbolics.division.spirit.vector.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.state.WingsEffectState;

public class SpiritWingsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final SpiritWingsModel<T> model;

    public SpiritWingsFeatureRenderer(FeatureRendererContext<T, M> context, ModelPart root) {
        super(context);
        this.model = new SpiritWingsModel<>(root);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        // need a data tracker to check state for other players
        if (entity instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
//                if (!sv.stateManager().isActive(WingsEffectState.ID)) return;
                matrices.push();
                matrices.translate(0, 0, 0.3f);
                VertexConsumer vc = ItemRenderer.getArmorGlintConsumer(
                        vertexConsumers, RenderLayer.getEntityTranslucent(sv.getSFX().wingsTexture()), false
                );
                this.model.setAngles(entity, limbAngle, limbDistance, entity.age, headYaw, headPitch, tickDelta);
                this.model.render(matrices, vc, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            });
        }
    }
}
