package symbolics.division.spirit.vector.sfx;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class EngineSoundInstance extends MovingSoundInstance {
    protected EngineSoundInstance(SoundEvent soundEvent, SoundCategory soundCategory, Random random) {
        super(soundEvent, soundCategory, random);
    }

    @Override
    public void tick() {

    }
}
