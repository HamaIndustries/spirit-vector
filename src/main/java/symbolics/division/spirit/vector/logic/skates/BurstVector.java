package symbolics.division.spirit.vector.logic.skates;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class BurstVector extends SpiritVector {
    public BurstVector(LivingEntity user, ItemStack itemStack) {
        super(user, itemStack, VectorType.BURST);
    }
}
