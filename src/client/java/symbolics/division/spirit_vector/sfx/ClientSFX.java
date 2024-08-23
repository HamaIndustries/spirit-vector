package symbolics.division.spirit_vector.sfx;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.sfx.particle.FeatherParticle;
import symbolics.division.spirit_vector.sfx.particle.RuneParticle;
import symbolics.division.spirit_vector.sfx.particle.SpiritParticle;

import java.util.HashMap;
import java.util.Map;

public class ClientSFX {

    private static final Map<Identifier, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType>> overrides = new HashMap<>();

    static {
        overrides.put(SpiritVectorSFX.BIRD.id, FeatherParticle.FeatherParticleFactory::new);
        overrides.put(SpiritVectorMod.id("angel"), RuneParticle.RuneParticleFactory::new);
        overrides.put(SpiritVectorMod.id("familiar"), RuneParticle.EmberParticleFactory::new);
        overrides.put(SpiritVectorMod.id("clover"), FeatherParticle.FeatherParticleFactory::new);
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

    public static PositionedSoundInstance cassette(SoundEvent sound, Vec3d pos) {
        return new PositionedSoundInstance(
                sound.getId(), SoundCategory.RECORDS, 4, 1, SoundInstance.createRandom(), true, 0, SoundInstance.AttenuationType.LINEAR, pos.x, pos.y, pos.z, false
        );
    }
}
