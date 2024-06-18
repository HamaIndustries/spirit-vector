package symbolics.division.spirit.vector.logic.ability;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.item.DreamRuneItem;

import static net.minecraft.registry.Registries.ITEM;

public class SpiritVectorAbilitiesRegistry {

    public static final RegistryKey<Registry<SpiritVectorAbility>> KEY = RegistryKey.ofRegistry(SpiritVectorMod.id("abilities"));

    private static final Registry<SpiritVectorAbility> INSTANCE = FabricRegistryBuilder
            .from(new SimpleRegistry<>(KEY, Lifecycle.stable(), false))
            .buildAndRegister();

    public static void registerAbility(Identifier id, SpiritVectorAbility ability) {
        Registry.register(INSTANCE, id, ability);
    }

    public static void init() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, SpiritVectorMod.id("spirit_vector_held_abilities"), SpiritVectorHeldAbilities.COMPONENT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, SpiritVectorMod.id("spirit_vector_ability"), SpiritVectorAbility.COMPONENT);
        registerAbility(SpiritVectorAbility.ID_NONE, SpiritVectorAbility.NONE);
    }

    public static Registry<SpiritVectorAbility> instance() {
        return INSTANCE;
    }

    public static void registerRuneAndAbility(Identifier id, SpiritVectorAbility ability) {
        registerAbility(id, ability);
        Registry.register(ITEM, id + "_rune", new DreamRuneItem(ability));
    }

}
