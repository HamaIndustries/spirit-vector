package symbolics.division.spirit.vector;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SpiritVectorSounds {
    public static final Identifier BURST_ID = SpiritVectorMod.id("burst");
    public static final SoundEvent BURST = SoundEvent.of(BURST_ID);
    public static final Identifier STEP_ID = SpiritVectorMod.id("step");
    public static final SoundEvent STEP = SoundEvent.of(STEP_ID);
    public static final Identifier SLIDE_ID = SpiritVectorMod.id("slide");
    public static final SoundEvent SLIDE = SoundEvent.of(SLIDE_ID);
    public static final Identifier SLIDE_START_ID = SpiritVectorMod.id("slide_start");
    public static final SoundEvent SLIDE_START = SoundEvent.of(SLIDE_START_ID);
    public static final Identifier ENGINE_ID = SpiritVectorMod.id("engine");
    public static final SoundEvent ENGINE = SoundEvent.of(ENGINE_ID);

    public static void init() {
        register(BURST_ID, BURST);
        register(STEP_ID, STEP);
        register(SLIDE_ID, SLIDE);
        register(SLIDE_START_ID, SLIDE_START);
        register(ENGINE_ID, ENGINE);
    }

    private static void register(Identifier id, SoundEvent event) {
        System.out.println("regidering sound with id " + id);
        Registry.register(Registries.SOUND_EVENT, id, event);
    }
}
