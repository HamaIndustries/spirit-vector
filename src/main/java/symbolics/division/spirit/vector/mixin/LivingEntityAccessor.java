package symbolics.division.spirit.vector.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("jumping")
    boolean spirit_vector$isJumping();

    @Invoker("getJumpVelocity")
    float spirit_vector$invokeGetJumpVelocity();
}
