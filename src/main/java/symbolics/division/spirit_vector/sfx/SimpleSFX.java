package symbolics.division.spirit_vector.sfx;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.Item;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Identifier;
import symbolics.division.spirit_vector.item.SFXPackItem;

public class SimpleSFX implements SFXPack<SimpleParticleType> {
    protected final SimpleParticleType PARTICLE_TYPE = FabricParticleTypes.simple();
    protected final Identifier wingsTexture;
    public final Identifier id;
    protected final int color;

    public SimpleSFX(Identifier id) { this(id, 0xffffff); }
    public SimpleSFX(Identifier id, int color) { this(id, color, "textures/wing/"); }
    public SimpleSFX(Identifier id, int color, String wingTexturePath) {
        this.wingsTexture = Identifier.of(id.getNamespace(),  wingTexturePath + id.getPath() + ".png");
        this.id = id;
        this.color = color;
    }

    @Override
    public Identifier wingsTexture() {
        return wingsTexture;
    }

    @Override
    public SimpleParticleType particleType() {
        return PARTICLE_TYPE;
    }

    @Override
    public SimpleParticleType particleEffect() { return PARTICLE_TYPE; }

    @Override
    public int color() {
        return this.color;
    }

    public Item asItem() {
        return new SFXPackItem(SFXRegistry.INSTANCE.getEntry(this));
    }
}
