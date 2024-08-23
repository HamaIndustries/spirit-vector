package symbolics.division.spirit_vector.logic.ability;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.item.DreamRuneItem;

import static net.minecraft.registry.Registries.ITEM;

public class SpiritVectorAbilitiesRegistry {

    public static final RegistryKey<Registry<SpiritVectorAbility>> KEY = RegistryKey.ofRegistry(SpiritVectorMod.id("abilities"));

    private static final Registry<SpiritVectorAbility> INSTANCE = FabricRegistryBuilder
            .from(new SimpleRegistry<>(KEY, Lifecycle.stable(), false))
            .buildAndRegister();

    public static void registerAbility(Identifier id, SpiritVectorAbility ability) {
        Registry.register(INSTANCE, id, ability);
    }

    static {
        Registry.register(Registries.DATA_COMPONENT_TYPE, SpiritVectorMod.id("spirit_vector_held_abilities"), SpiritVectorHeldAbilities.COMPONENT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, SpiritVectorMod.id("spirit_vector_ability"), SpiritVectorAbility.COMPONENT);
        registerAbility(SpiritVectorAbility.ID_NONE, SpiritVectorAbility.NONE);
    }

    public static void init() {
//        nullRune = registerRuneAndAbility(SpiritVectorAbility.ID_NONE, SpiritVectorAbility.NONE);
    }

    public static Registry<SpiritVectorAbility> instance() {
        return INSTANCE;
    }

    public static DreamRuneItem registerRuneAndAbility(Identifier id, SpiritVectorAbility ability) {
        registerAbility(id, ability);
        return Registry.register(ITEM, id.withPrefixedPath("spirit_rune_"), new DreamRuneItem(ability));
    }

    @NotNull
    public static DreamRuneItem registerNullItem() {
        return Registry.register(ITEM, SpiritVectorMod.id("spirit_rune_null"), new DreamRuneItem(SpiritVectorAbility.NONE));
    }

}
