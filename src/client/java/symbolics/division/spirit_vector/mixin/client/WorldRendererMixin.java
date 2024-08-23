package symbolics.division.spirit_vector.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.spirit_vector.SpiritVectorSounds;
import symbolics.division.spirit_vector.sfx.ClientSFX;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @WrapOperation(
            method = "playJukeboxSong",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/sound/PositionedSoundInstance;record(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/sound/PositionedSoundInstance;"
            )
    )
    private PositionedSoundInstance injectCassetteEvent(SoundEvent sound, Vec3d pos, Operation<PositionedSoundInstance> op) {
        if (SpiritVectorSounds.doesSoundLoop(sound)) {
            return ClientSFX.cassette(sound, pos);
        }
        return op.call(sound, pos);
    }
}
