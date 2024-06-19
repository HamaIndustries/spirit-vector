package symbolics.division.spirit.vector;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import java.util.function.Function;

public class SpiritVectorRegistration {
    public static <T> ComponentType<T> registerComponent(String id, Function<ComponentType.Builder<T>, ComponentType.Builder<T>> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, SpiritVectorMod.id(id), builder.apply(ComponentType.<T>builder()).build());
    }
}
