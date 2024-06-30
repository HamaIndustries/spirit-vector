package symbolics.division.spirit.vector.sfx;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.sfx.particle.FeatherParticle;
import symbolics.division.spirit.vector.sfx.particle.RuneParticle;
import symbolics.division.spirit.vector.sfx.particle.SpiritParticle;

import java.util.HashMap;
import java.util.Map;

public class ClientSFX {

    private static final Map<Identifier, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType>> overrides = new HashMap<>();

    static {
        overrides.put(SpiritVectorSFX.BIRD.id, FeatherParticle.FeatherParticleFactory::new);
        overrides.put(SpiritVectorMod.id("angel"), RuneParticle.RuneParticleFactory::new);
    }

    public static void registerAll() {
        for (SimpleSFX sfx : SpiritVectorSFX.getSimpleSFX()) {
            ParticleFactoryRegistry.getInstance().register(sfx.particleType(),
                    overrides.getOrDefault(sfx.id, SpiritParticle.SpiritParticleFactory::new)
            );
        }
        for (SimpleSFX sfx : SpiritVectorSFX.getUniqueSFX().values()) {
            ParticleFactoryRegistry.getInstance().register(sfx.particleType(),
                    overrides.getOrDefault(sfx.id, SpiritParticle.SpiritParticleFactory::new)
            );
        }
    }
}
