package symbolics.division.spirit.vector.sfx;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.GlowParticle;

public class ClientSFX {

    public static final SimpleSFX[] simpleSFX = {
            SFXPack.BUTTERFLY
    };

    public static void registerAll() {
        for (SimpleSFX sfx : simpleSFX) {
            ParticleFactoryRegistry.getInstance().register(sfx.particleType(), GlowParticle.GlowFactory::new);
        }
    }
}
