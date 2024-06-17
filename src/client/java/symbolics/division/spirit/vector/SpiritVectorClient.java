package symbolics.division.spirit.vector;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import symbolics.division.spirit.vector.render.SpiritWingsFeatureRenderer;
import symbolics.division.spirit.vector.render.SpiritWingsModel;
import symbolics.division.spirit.vector.sfx.ClientSFX;

public class SpiritVectorClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// spirit wings reg
		EntityModelLayerRegistry.registerModelLayer(SpiritWingsModel.LAYER, SpiritWingsModel::getTexturedModelData);
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
				(entityType, entityRenderer, registrationHelper, context) -> {
					if (entityRenderer instanceof PlayerEntityRenderer) {
						registrationHelper.register(new SpiritWingsFeatureRenderer<>(entityRenderer, context.getModelLoader()));
					}
				}
		);

		// particles reg
		ClientSFX.registerAll();
	}
}