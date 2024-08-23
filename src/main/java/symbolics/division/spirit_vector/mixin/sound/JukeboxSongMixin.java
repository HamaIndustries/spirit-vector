package symbolics.division.spirit_vector.mixin.sound;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit_vector.SpiritVectorSounds;

@Mixin(JukeboxSong.class)
public abstract class JukeboxSongMixin {

    @Shadow @Final
    public RegistryEntry<SoundEvent> soundEvent;

    @Inject(method = "shouldStopPlaying", at = @At("HEAD"), cancellable = true)
    public void shouldStopPlaying(long ticksPlaying, CallbackInfoReturnable<Boolean> ci) {
        if (SpiritVectorSounds.doesSoundLoop(soundEvent.value())) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
