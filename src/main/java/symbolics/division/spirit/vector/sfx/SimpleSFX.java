package symbolics.division.spirit.vector.sfx;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.Item;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Identifier;

public class SimpleSFX implements SFXPack<SimpleParticleType> {
    protected final SimpleParticleType PARTICLE_TYPE = FabricParticleTypes.simple();
    protected final Identifier wingsTexture;
    public final Identifier id;

    public SimpleSFX(Identifier id) {
        this.wingsTexture = Identifier.of(id.getNamespace(),  "textures/wing/" + id.getPath() + ".png");
        this.id = id;
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

    public Item asItem() {
        return new Item(new Item.Settings().component(COMPONENT, SFXRegistry.INSTANCE.getEntry(this)));
    }
}
