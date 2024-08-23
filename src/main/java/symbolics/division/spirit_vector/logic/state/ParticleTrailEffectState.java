package symbolics.division.spirit_vector.logic.state;

import net.minecraft.util.Identifier;
import symbolics.division.spirit_vector.SpiritVectorMod;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;

public class ParticleTrailEffectState extends ManagedState {
    public static final Identifier ID = SpiritVectorMod.id("particle_trail");

    public ParticleTrailEffectState(SpiritVector sv) {
        super(sv);
    }

    @Override
    public void tick() {
        var user = sv.user;
//        int particleRate = 3 - (3 * sv.getMomentum() / SpiritVector.MAX_MOMENTUM);
        sv.effectsManager().spawnParticle(user.getWorld(), user.getPos().add(0, 1, 0).subtract(user.getVelocity().normalize()).addRandom(user.getRandom(), 1));
        super.tick();
    }
}
