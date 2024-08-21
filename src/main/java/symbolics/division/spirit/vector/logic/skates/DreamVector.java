package symbolics.division.spirit.vector.logic.skates;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class DreamVector extends SpiritVector {
    public DreamVector(LivingEntity user, ItemStack itemStack) {
        super(user, itemStack, VectorType.DREAM);
    }
}
