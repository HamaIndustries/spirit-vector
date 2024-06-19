package symbolics.division.spirit.vector.logic.state;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.SpiritVector;

public class ParticleTrailEffectState extends AbstractManagedState {
    public static final Identifier ID = SpiritVectorMod.id("particle_trail");

    public ParticleTrailEffectState(SpiritVector sv) {
        super(sv);
    }

    @Override
    public void tick() {
        var user = sv.user;
        sv.getEffectsManager().spawnParticle(user.getWorld(), user.getPos().add(0, 1, 0).subtract(user.getVelocity().normalize()).addRandom(user.getRandom(), 1));
        super.tick();
    }
}
