package symbolics.division.spirit.vector.mixin;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.JumpMovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

    // cache instanceof check
    // maybe generalize into caching object
    public boolean checked = false;
    public boolean isSVUser = false;
    @Nullable
    public SpiritVector maybeGetSpiritVector() {
        if (!checked) {
            checked = true;
            if (this instanceof ISpiritVectorUser user) {
                isSVUser = true;
            }
        }
        if (isSVUser) {
            return ((ISpiritVectorUser)this).spiritVector();
        }
        return null;
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        if (this instanceof ISpiritVectorUser user) {
            if (!this.isLogicalSideForUpdatingMovement() || this.isInFluid() || this.hasVehicle() || ((LivingEntity)(Entity)this).isFallFlying()) {
                return;
            }
            user.getSpiritVector().ifPresent(sv -> sv.travel(movementInput, ci));
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

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jump(CallbackInfo ci) {
        SpiritVector sv = maybeGetSpiritVector();
        if (sv != null) {
            sv.jump(new JumpMovementContext(ci));
        }
    }



    @Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true)
    public void getStepHeight(CallbackInfoReturnable<Float> ci) {
        // skates let you roll over slopes ig
        if (SpiritVector.hasEquipped((LivingEntity)(Entity)this)) {
            ci.setReturnValue(1.2f);
            ci.cancel();
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    public void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> ci) {
        if (SpiritVector.hasEquipped((LivingEntity)(Entity)this)) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }

    @Inject(method = "hasNoDrag", at = @At("HEAD"), cancellable = true)
    public void hasNoDrag(CallbackInfoReturnable<Boolean> ci) {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                if (sv.getMoveState().disableDrag(sv)) {
                    ci.setReturnValue(true);
                    ci.cancel();
                }
            });
        }
    }

}
