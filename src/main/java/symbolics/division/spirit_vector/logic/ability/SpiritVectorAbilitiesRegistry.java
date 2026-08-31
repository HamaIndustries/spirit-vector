package symbolics.division.spirit_vector.logic.ability;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import symbolics.division.spirit_vector.SpiritVectorItems;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.item.DreamRuneItem;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.registry.Registries.ITEM;

public class SpiritVectorAbilitiesRegistry {
	protected static final Map<SpiritVectorAbility, DreamRuneItem> RUNE_LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<SpiritVectorAbility>> KEY = RegistryKey.ofRegistry(SpiritVectorMod.id("abilities"));

    private static final Registry<SpiritVectorAbility> INSTANCE = FabricRegistryBuilder
            .from(new SimpleRegistry<>(KEY, Lifecycle.stable(), false))
			.attribute(RegistryAttribute.SYNCED)
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
		DreamRuneItem item = Registry.register(ITEM, id.withPrefixedPath("spirit_rune_"), new DreamRuneItem(ability));
		RUNE_LOOKUP.put(ability, item);
        return item;
    }

    @NotNull
    public static DreamRuneItem registerNullItem() {
        return Registry.register(ITEM, SpiritVectorMod.id("spirit_rune_null"), new DreamRuneItem(SpiritVectorAbility.NONE));
    }

	@Nullable
	public static DreamRuneItem getRuneForAbility(SpiritVectorAbility ability) {
		return RUNE_LOOKUP.getOrDefault(ability, null);
	}

}
