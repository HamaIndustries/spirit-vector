package symbolics.division.spirit.vector;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.IceBlock;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbilitiesRegistry;
import symbolics.division.spirit.vector.sfx.SpiritVectorSFX;

public final class SpiritVectorMod implements ModInitializer {
	public static final String MODID = "spirit_vector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String identifier) { return Identifier.of(MODID, identifier); }

	@Override
	public void onInitialize() {
		SpiritVectorAbilitiesRegistry.init();
		SpiritVectorSFX.registerAll();
		SpiritVectorItems.init();
	}
}