package symbolics.division.spirit.vector.sfx;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SpiritVectorSFX {
    public static final SFXPack<?>[] ALL_SFX = {
            SFXPack.BUTTERFLY
    };

    // handle sfx registration
    public static void registerAll() {
        for (var sfx : ALL_SFX) {
            Registry.register(Registries.PARTICLE_TYPE, sfx.id(), sfx.particleType());
        }
    }

    public static SFXPack<?> DEFAULT_SFX = SFXPack.BUTTERFLY;
}
