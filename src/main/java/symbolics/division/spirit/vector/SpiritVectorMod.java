package symbolics.division.spirit.vector;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.spirit.vector.logic.SVEntityState;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbilitiesRegistry;
import symbolics.division.spirit.vector.sfx.EffectsManager;
import symbolics.division.spirit.vector.sfx.SFXRequestPayload;

public final class SpiritVectorMod implements ModInitializer {
	public static final String MODID = "spirit_vector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String identifier) { return Identifier.of(MODID, identifier); }

	@Override
	public void onInitialize() {
		SpiritVectorAbilitiesRegistry.init();
		SpiritVectorItems.init();

		PayloadTypeRegistry.playC2S().register(SFXRequestPayload.ID, SFXRequestPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SFXRequestPayload.ID, EffectsManager::acceptC2SPayload);

		// if this happens more often, may need a factory for generalizing this over record attachments
		PayloadTypeRegistry.playC2S().register(SVEntityState.Payload.ID, SVEntityState.Payload.CODEC);
		PayloadTypeRegistry.playS2C().register(SVEntityState.Payload.ID, SVEntityState.Payload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SVEntityState.Payload.ID, SVEntityState::handleStateSyncC2S);

		// hoping this covers all cases where players should be updated on wing state
		// TODO doesn't seem to work, figure out during integration with larger sample size
		ServerEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
			if (entity instanceof ServerPlayerEntity player) SVEntityState.updatePlayerStatesOnSpawn(player);
		}));
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> SVEntityState.updatePlayerStatesOnSpawn(handler.player)));

	}
}