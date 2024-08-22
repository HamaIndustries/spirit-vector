package symbolics.division.spirit.vector.logic.vector;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class DreamVector extends SpiritVector {

    public static final float MOMENTUM_GAIN_SPEED = 1.0f;

    public DreamVector(LivingEntity user, ItemStack itemStack) {
        super(user, itemStack, VectorType.DREAM);
    }

    @Override
    public float getStepHeight() {
        float base = super.getStepHeight();
        return isSoaring() ? base * 2 : base;
    }


}
