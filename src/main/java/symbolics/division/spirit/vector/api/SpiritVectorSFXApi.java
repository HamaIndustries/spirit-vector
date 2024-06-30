package symbolics.division.spirit.vector.api;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.sfx.SFXPack;
import symbolics.division.spirit.vector.sfx.SFXRegistry;
import symbolics.division.spirit.vector.sfx.SimpleSFX;

public class SpiritVectorSFXApi {
    public static SimpleSFX registerSimple(Identifier id, int color, String wingsTexturePath) {
        SimpleSFX pack = new SimpleSFX(id, color, wingsTexturePath);
        Registry.register(Registries.PARTICLE_TYPE, id, pack.particleType());
        return register(pack, id);
    }

    public static SimpleSFX registerSimple(Identifier id, int color) {
        // default path is textures/wing/id_path.png
        SimpleSFX pack = new SimpleSFX(id, color);
        Registry.register(Registries.PARTICLE_TYPE, id, pack.particleType());
        return register(pack, id);
    }

    public static SimpleSFX registerSimple(Identifier id) {
        // default color is white
        SimpleSFX pack = new SimpleSFX(id);
        Registry.register(Registries.PARTICLE_TYPE, id, pack.particleType());
        return register(pack, id);
    }

    public static <T extends SFXPack<?>> T register(T pack, Identifier id) {
        return Registry.register(SFXRegistry.INSTANCE, id, pack);
    }

    // after sfx pack is registered, you can make an item out of it with SFXPack#asItem.
    // remember to register it to SpiritVectorTags.Items.SFX_PACK_TEMPLATES tag for crafting.
}
