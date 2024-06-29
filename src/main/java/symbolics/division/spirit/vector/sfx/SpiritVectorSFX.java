package symbolics.division.spirit.vector.sfx;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.api.SpiritVectorSFXApi;

import java.util.ArrayList;
import java.util.List;

public final class SpiritVectorSFX {
    // Spirit Vector's default SFX.
    // use the API to add your own.

    private static final List<SimpleSFX> simpleSFX = new ArrayList<>();

    public static final SimpleSFX BUTTERFLY = registerSimple("butterfly", 0xe68a25);
    public static final SimpleSFX BIRD = registerSimple("bird", 0x4cc2e0);
    public static final SimpleSFX V1 = registerSimple("v1", 0xebbd33);
    public static final SimpleSFX ROBO = registerSimple("robo", 0x54e5ac);
    public static final SimpleSFX DRAGON = registerSimple("dragon", 0xe14d2f);
    public static final SimpleSFX LOVE = registerSimple("love", 0xed3299);
    public static final SimpleSFX ANGEL = registerSimple("angel", 0xffffff);

    private static SimpleSFX registerSimple(String name, int color) {
        Identifier id = SpiritVectorMod.id(name);
        SimpleSFX pack = SpiritVectorSFXApi.registerSimple(id, color);
        simpleSFX.add(pack);
        return pack;
    }

    public static SFXPack<?> getDefault() { return BUTTERFLY; }

    public static List<SimpleSFX> getSimpleSFX() {
        return simpleSFX.stream().toList();
    }
}
