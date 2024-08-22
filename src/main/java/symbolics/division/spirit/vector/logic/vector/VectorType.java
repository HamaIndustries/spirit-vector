package symbolics.division.spirit.vector.logic.vector;

import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;

import static symbolics.division.spirit.vector.registry.SpiritVectorRegistration.Registrars;

public record VectorType(Identifier id, SpiritVectorFactory factory) {

    public static final Registry<VectorType> REGISTRY = Registrars.VECTOR_TYPE.registry();
    public static final ComponentType<RegistryEntry<VectorType>> COMPONENT = Registrars.VECTOR_TYPE.component();

    public static final VectorType DREAM = of("dream", DreamVector::new);
//    public static final VectorType BURST = of("burst", BurstVector::new);
    public static final VectorType SPIRIT = of("spirit", SpiritVector::new);

    @FunctionalInterface
    public interface SpiritVectorFactory {
        SpiritVector make(LivingEntity user, ItemStack itemStack);
    }

    private static VectorType of(String name, SpiritVectorFactory factory) {
        var id = SpiritVectorMod.id(name);
        return Registry.register(REGISTRY, id, new VectorType(id, factory));
    }

    public static VectorType getFromStack(ItemStack itemStack) {
        return itemStack.getOrDefault(COMPONENT, REGISTRY.getEntry(SPIRIT)).value();
    }

    public static void init(){}
}
