package symbolics.division.spirit.vector;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	}
}