package symbolics.division.spirit.vector.sfx;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;

import java.util.ArrayList;
import java.util.List;

public class SpiritVectorSFX {
    public static final List<SimpleSFX> simpleSFX = new ArrayList<>();

    public static final SimpleSFX BUTTERFLY = registerSimple("butterfly");
    public static final SimpleSFX BIRD = registerSimple("bird");

    private static SimpleSFX registerSimple(String name) {
        Identifier id = SpiritVectorMod.id(name);
        SimpleSFX pack = register(new SimpleSFX(id), id);
        simpleSFX.add(pack);
        return pack;
    }

    private static <T extends SFXPack<?>> T register(T pack, Identifier id) {
        Registry.register(Registries.PARTICLE_TYPE, id.withSuffixedPath("_particle"), pack.particleType());
        return Registry.register(SFXRegistry.INSTANCE, id, pack);
    }

    public static SFXPack<?> getDefault() { return BUTTERFLY; }
}
