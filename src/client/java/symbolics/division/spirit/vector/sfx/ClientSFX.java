package symbolics.division.spirit.vector.sfx;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.SimpleParticleType;

import java.util.HashMap;
import java.util.Map;

public class ClientSFX {

    private static Map<SimpleSFX, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType>> overrides = new HashMap<>();

    static {
        overrides.put(SpiritVectorSFX.BIRD, FeatherParticle.FeatherParticleFactory::new);
    }

    public static void registerAll() {
        for (SimpleSFX sfx : SpiritVectorSFX.getSimpleSFX()) {
            ParticleFactoryRegistry.getInstance().register(sfx.particleType(),
                    overrides.getOrDefault(sfx, SpiritParticle.SpiritParticleFactory::new)
            );
        }
    }
}
