package symbolics.division.spirit.vector;

import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiritVector implements ModInitializer {
	public static final String MODID = "spirit_vector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		SpiritVectorItems.init();
	}
}