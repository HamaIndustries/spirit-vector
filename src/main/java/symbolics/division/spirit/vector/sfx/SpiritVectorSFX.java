package symbolics.division.spirit.vector.sfx;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;

import java.util.ArrayList;
import java.util.List;

public final class SpiritVectorSFX {
    private static final List<SimpleSFX> simpleSFX = new ArrayList<>();

    public static final SimpleSFX BUTTERFLY = registerSimple("butterfly");
    public static final SimpleSFX BIRD = registerSimple("bird");
    public static final SimpleSFX V1 = registerSimple("v1");
    public static final SimpleSFX ROBO = registerSimple("robo");
    public static final SimpleSFX DRAGON = registerSimple("dragon");
    public static final SimpleSFX LOVE = registerSimple("love");
    public static final SimpleSFX ANGEL = registerSimple("angel");

    private static SimpleSFX registerSimple(String name) {
        Identifier id = SpiritVectorMod.id(name);
        SimpleSFX pack = register(new SimpleSFX(id), id);
        simpleSFX.add(pack);
        return pack;
    }

    private static <T extends SFXPack<?>> T register(T pack, Identifier id) {
        Registry.register(Registries.PARTICLE_TYPE, id, pack.particleType());
        return Registry.register(SFXRegistry.INSTANCE, id, pack);
    }

    public static SFXPack<?> getDefault() { return BUTTERFLY; }

    public static List<SimpleSFX> getSimpleSFX() {
        return simpleSFX.stream().toList();
    }

}
