package symbolics.division.spirit.vector;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.spirit.vector.logic.SVEntityState;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbilitiesRegistry;
import symbolics.division.spirit.vector.logic.ability.TeleportAbilityC2SPayload;
import symbolics.division.spirit.vector.sfx.EffectsManager;
import symbolics.division.spirit.vector.sfx.SFXRequestPayload;

public final class SpiritVectorMod implements ModInitializer {
	public static final String MODID = "spirit_vector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String identifier) { return Identifier.of(MODID, identifier); }
	public static <T extends CustomPayload> CustomPayload.Id<T> payloadId(String identififer) {
		return new CustomPayload.Id<>(id(identififer));
	}

	@Override
	public void onInitialize() {
		SpiritVectorAbilitiesRegistry.init();
		SpiritVectorItems.init();
		SpiritVectorSounds.init();

		PayloadTypeRegistry.playC2S().register(SFXRequestPayload.ID, SFXRequestPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SFXRequestPayload.ID, EffectsManager::acceptC2SPayload);

		registerC2S(
				TeleportAbilityC2SPayload.ID, TeleportAbilityC2SPayload.CODEC, TeleportAbilityC2SPayload::HANDLER
		);

		// if this happens more often, may need a factory for generalizing this over record attachments
		PayloadTypeRegistry.playC2S().register(SVEntityState.Payload.ID, SVEntityState.Payload.CODEC);
		PayloadTypeRegistry.playS2C().register(SVEntityState.Payload.ID, SVEntityState.Payload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SVEntityState.Payload.ID, SVEntityState::handleStateSyncC2S);
//		registerC2S(
//				SVEntityState.Payload.ID, SVEntityState.Payload.CODEC, SVEntityState::handleStateSyncC2S
//		);

		// hoping this covers all cases where players should be updated on wing state
		// TODO doesn't seem to work, figure out during integration with larger sample size
		ServerEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
			if (entity instanceof ServerPlayerEntity player) SVEntityState.updatePlayerStatesOnSpawn(player);
		}));
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> SVEntityState.updatePlayerStatesOnSpawn(handler.player)));
	}
	//<T extends CustomPayload> CustomPayload.Type<? super B, T>
	private <T extends CustomPayload>
	void registerC2S(CustomPayload.Id<T> pid, PacketCodec<? super RegistryByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
		PayloadTypeRegistry.playC2S().register(pid, codec);
		ServerPlayNetworking.registerGlobalReceiver(pid, handler);
	}

//	registerS2C
}