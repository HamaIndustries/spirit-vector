package symbolics.division.spirit.vector.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.move.LedgeVaultMovement;

import java.util.Arrays;

@Mixin(Entity.class)
public class EntityMixin {
    private static float VAULT_TRIGGER_STEP_DISTANCE = 0.3f;

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

    // then this is how we determine that a step up happened
    @Inject(
            method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("RETURN"),
            cancellable = true
    )
    public void imAWomansManNoTimeToTalk(Vec3d movement, CallbackInfoReturnable<Vec3d> ci) {
        Vec3d result = ci.getReturnValue();
        Entity entity = ((Entity)(Object)this);
        if (result.y >= VAULT_TRIGGER_STEP_DISTANCE && entity.isOnGround() && entity instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(LedgeVaultMovement::triggerLedge);
        }
    }

    @Inject(method = "onLanding", at = @At("HEAD"))
    public void onLanding(CallbackInfo ci) {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                // fake reset for chain jumps
                sv.onLanding();
            });
        }
    }
}
