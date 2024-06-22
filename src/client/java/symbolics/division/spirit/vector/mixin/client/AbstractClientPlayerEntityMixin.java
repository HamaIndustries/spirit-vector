package symbolics.division.spirit.vector.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit.vector.sfx.SlidingSoundInstance;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    private SlidingSoundInstance slidingSound;
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (slidingSound == null) {
            if (SlidingSoundInstance.shouldPlayFor((AbstractClientPlayerEntity) (PlayerEntity) this)) {
                slidingSound = new SlidingSoundInstance(this);
                MinecraftClient.getInstance().getSoundManager().play(slidingSound);
            }
        } else if (slidingSound.isDone()) {
            slidingSound = null;
        }
    }
}
