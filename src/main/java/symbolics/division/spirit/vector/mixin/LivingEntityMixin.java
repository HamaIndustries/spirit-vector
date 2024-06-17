package symbolics.division.spirit.vector.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Shadow public boolean jumping;

    // lets try overriding travel and reimplementing custom travel
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        if (this instanceof ISpiritVectorUser user) {
            if (!this.isLogicalSideForUpdatingMovement() || this.isInFluid() || this.hasVehicle() || ((LivingEntity)(Entity)this).isFallFlying()) {
                return;
            }
            if (user instanceof PlayerEntity player && player.getAbilities().flying) return;
            user.getSpiritVector().ifPresent(sv -> sv.travel(movementInput, ci, jumping));
        }
    }

    @Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
    public void getMovementSpeed(float slip, CallbackInfoReturnable<Float> ci)  {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                ci.setReturnValue(sv.getMovementSpeed(slip));
                ci.cancel();
            });
        }
    }

    @Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true)
    public void getStepHeight(CallbackInfoReturnable<Float> ci) {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                float s = sv.stepHeight();
                if (s > 0) {
                    ci.setReturnValue(s);
                    ci.cancel();
                }
            });
        }
    }
}
