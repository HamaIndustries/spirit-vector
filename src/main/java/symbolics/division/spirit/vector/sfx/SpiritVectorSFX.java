package symbolics.division.spirit.vector.sfx;

import net.minecraft.particle.SimpleParticleType;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.api.SpiritVectorSFXApi;

import java.util.*;

public final class SpiritVectorSFX {
    // Spirit Vector's default SFX.
    // use the API to add your own.

    private static final List<SimpleSFX> SIMPLE_SFX = new ArrayList<>();
    private static final Map<UUID, SimpleSFX> UNIQUE_SFX = new HashMap<>();

    public static final SimpleSFX BUTTERFLY = registerSimple("butterfly", 0xe68a25);
    public static final SimpleSFX BIRD = registerSimple("bird", 0x4cc2e0);
    public static final SimpleSFX V = registerSimple("v", 0xebbd33);
    public static final SimpleSFX ROBO = registerSimple("robo", 0x54e5ac);
    public static final SimpleSFX DRAGON = registerSimple("dragon", 0xe14d2f);
    public static final SimpleSFX LOVE = registerSimple("love", 0xed3299);

    static {
        registerUnique("angel", UUID.fromString("62d5f675-f2b1-48a3-b5b6-78127cd1ed2c"));
        registerUnique("zy", UUID.fromString("0af7b31f-63a5-426d-8cee-6c54385856b6"));
        registerUnique("familiar", UUID.fromString("97f88493-9d69-42f8-b1c8-aaab1e05c89f"));
        registerUnique("clover", UUID.fromString("4446f546-5c95-418d-9e9c-ea7efbfc31a3"));
        registerUnique("earth", UUID.fromString("688160b1-b946-4c18-9e65-097dff928f41"));
    }

    private static SimpleSFX registerSimple(String name, int color) {
        SimpleSFX pack = SpiritVectorSFXApi.registerSimple(SpiritVectorMod.id(name), color);
        SIMPLE_SFX.add(pack);
        return pack;
    }

    private static SFXPack<?> registerUnique(String name, UUID bind) {
        SimpleSFX pact = SpiritVectorSFXApi.registerSimple(
                SpiritVectorMod.id(name),
                0xffffff,
                "textures/wing/unique/"
        );
        UNIQUE_SFX.put(bind, pact);
        return pact;
    }

    public static SFXPack<?> getDefault() { return BIRD; }

    public static List<SimpleSFX> getSimpleSFX() {
        return SIMPLE_SFX.stream().toList();
    }
    public static Map<UUID, SimpleSFX> getUniqueSFX() {
        return Collections.unmodifiableMap(UNIQUE_SFX);
    }
}
