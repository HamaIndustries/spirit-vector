package symbolics.division.spirit.vector;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import symbolics.division.spirit.vector.event.JukeboxEvent;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SVEntityState;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.ability.SlamPacketC2S;
import symbolics.division.spirit.vector.logic.ability.TeleportAbilityC2SPayload;
import symbolics.division.spirit.vector.networking.ModifyMomentumPayloadS2C;
import symbolics.division.spirit.vector.render.SpiritGaugeHUD;
import symbolics.division.spirit.vector.render.SpiritVectorSkatesRenderer;
import symbolics.division.spirit.vector.render.SpiritWingsFeatureRenderer;
import symbolics.division.spirit.vector.render.SpiritWingsModel;
import symbolics.division.spirit.vector.sfx.ClientSFX;
import symbolics.division.spirit.vector.sfx.EffectsManager;

public class SpiritVectorClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		/// pardon the dust.

		EffectsManager.registerSFXRequestC2SCallback(ClientPlayNetworking::send);
//		registerS2C(
//				SVEntityState.Payload.ID, SVEntityState.Payload.CODEC,
//				(payload, context) -> SVEntityState.handleStateSync(payload, context.player().getWorld())
//		);

		ClientPlayNetworking.registerGlobalReceiver(
				SVEntityState.Payload.ID, (payload, context) -> SVEntityState.handleStateSync(payload, context.player().getWorld())
		);

		ClientPlayNetworking.registerGlobalReceiver(
				ModifyMomentumPayloadS2C.ID,
				((payload, context) -> ModifyMomentumPayloadS2C.HANDLER(payload, context.player()))
		);

		// spirit wings reg
		EntityModelLayerRegistry.registerModelLayer(SpiritWingsModel.LAYER, SpiritWingsModel::getTexturedModelData);

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
			(entityType, entityRenderer, registrationHelper, context) -> {
				if (entityRenderer instanceof PlayerEntityRenderer) {
					ModelPart wings = context.getPart(SpiritWingsModel.LAYER);
					registrationHelper.register(new SpiritWingsFeatureRenderer<>(entityRenderer, wings));
				}
			}
		);

		// skates
		ArmorRenderer.register(new SpiritVectorSkatesRenderer(), SpiritVectorItems.SPIRIT_VECTOR);

		// particles req
		ClientSFX.registerAll();
		ClientTickEvents.START_CLIENT_TICK.register(InputHandler::tick);

		// teleport ability req
		TeleportAbilityC2SPayload.registerRequestCallback(
				p -> ClientPlayNetworking.send(new TeleportAbilityC2SPayload(p))
		);

		// space jam
		SlamPacketC2S.registerRequestCallback(
				p -> ClientPlayNetworking.send(new SlamPacketC2S((p)))
		);

		HudRenderCallback.EVENT.register(SpiritGaugeHUD::onHudRender);

		JukeboxEvent.PLAY.register(((world, jukeboxManager, pos) -> {
			if (MinecraftClient.getInstance().player instanceof ISpiritVectorUser user) {
				var sv = user.spiritVector();
				if (sv != null && pos.isWithinDistance(sv.user.getPos(), 32)) {
					sv.modifyMomentum(SpiritVector.MAX_MOMENTUM / 10);
					sv.stateManager().enableStateFor(SpiritVector.POISE_DECAY_GRACE_STATE, 20);
				}
			}
		}));
	}

//	private <T extends CustomPayload>
//	void registerS2C(CustomPayload.Id<T> pid, PacketCodec<? super RegistryByteBuf, T> codec, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
//		PayloadTypeRegistry.playC2S().register(pid, codec);
//		ClientPlayNetworking.registerGlobalReceiver(pid, handler);
//	}
}