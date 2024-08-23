package symbolics.division.spirit_vector.registry;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.logic.ability.SpiritVectorAbilitiesRegistry;
import symbolics.division.spirit_vector.logic.vector.VectorType;

import java.util.function.Function;

public class SpiritVectorRegistration {

    public static <T> ComponentType<T> registerComponent(String id, Function<ComponentType.Builder<T>, ComponentType.Builder<T>> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, SpiritVectorMod.id(id), builder.apply(ComponentType.<T>builder()).build());
    }

    public static class Registrars {
        public static final Registrar<VectorType> VECTOR_TYPE = Registrar.of("vector_type");
    }

    public static void init() {
        SpiritVectorAbilitiesRegistry.init();
        VectorType.init();
    }
}
