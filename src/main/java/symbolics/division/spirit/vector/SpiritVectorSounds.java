package symbolics.division.spirit.vector;

import net.minecraft.registry.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SpiritVectorSounds {

    public static final SoundEvent BURST = register("sfx.burst");
    public static final SoundEvent STEP = register("sfx.step");
    public static final SoundEvent SLIDE = register("sfx.slide");
    public static final SoundEvent SLIDE_START = register("sfx.slide_start");
    public static final SoundEvent ENGINE = register("sfx.engine");

    public static final SoundEvent TAKE_BREAK_SONG = register("cassette.take_break");
    public static final SoundEvent SHOW_DONE_SONG = register("cassette.show_done");

    private static SoundEvent register(String id) {
        return registerWithId(SpiritVectorMod.id(id));
    }

    private static SoundEvent registerWithId(Identifier id) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
