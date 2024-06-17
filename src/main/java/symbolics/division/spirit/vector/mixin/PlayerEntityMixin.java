package symbolics.division.spirit.vector.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    public void clipAtLedge(CallbackInfoReturnable<Boolean> ci) {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(sv -> {
                if (sv.clipAtLedge()) {
                    ci.setReturnValue(false);
                    ci.cancel();
                }
            });
        }
    }
}
