package symbolics.division.spirit.vector.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.SpiritVectorItems;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;

import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements ISpiritVectorUser {
    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    public SpiritVector spiritVector;
    @Override
    public Optional<SpiritVector> getSpiritVector() {
        ItemStack item = getEquippedStack(EquipmentSlot.FEET);
        if (item.isOf(SpiritVectorItems.SPIRIT_VECTOR)) {
            if (spiritVector == null) {
                spiritVector = new SpiritVector((LivingEntity)(Entity)this);
            }
            return Optional.of(spiritVector);
        } else {
            spiritVector = null;
            return Optional.empty();
        }
    }



    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    public void tickMovement(CallbackInfo ci) {
//        getSpiritVector().ifPresent(sv -> {
//            var ctx = new MovementContext(this.lastVelocity);
//            sv.preApplyMovementInput(ctx);
//            if (ctx.isCancel()) {
////                ci.cancel();
//
//            }
//        });
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    public void shouldSlowDown(CallbackInfoReturnable<Boolean> ci) {
        getSpiritVector().ifPresent(sv -> {
            if (sv.preventSlowdown()) {
                ci.setReturnValue(false);
                ci.cancel();
            }
        });
    }
}
