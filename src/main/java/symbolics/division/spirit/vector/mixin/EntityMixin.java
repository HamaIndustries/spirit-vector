package symbolics.division.spirit.vector.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.move.LedgeVaultMovement;

@Mixin(Entity.class)
public class EntityMixin {
    // tells the "physics system" (lol) that we're on the ground for the purpose of ledge climbing
    @WrapOperation(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnGround()Z")
    )
    public boolean youCanTellByTheWayIUseMyWalk(Entity instance, Operation<Boolean> value) {
        if (((Entity)(Object)this) instanceof LivingEntity entity && SpiritVector.hasEquipped(entity)) {
            return true;
        } else {
            return value.call(instance);
        }
    }

    // then I think this is actually how we determine that a step up happened
    @ModifyExpressionValue(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;collectStepHeights(Lnet/minecraft/util/math/Box;Ljava/util/List;FF)[F")
    )
    public float[] imAWomansManNoTimeToTalk(float[] result) {
        if (result.length > 0 && ((Entity)(Object)this) instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(LedgeVaultMovement::triggerLedge);
        }
        return result;
    }
}
