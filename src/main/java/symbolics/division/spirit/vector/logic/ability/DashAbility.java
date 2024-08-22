package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.move.MovementType;
import symbolics.division.spirit.vector.logic.state.ParticleTrailEffectState;

public class DashAbility extends AbstractSpiritVectorAbility {
    public static final float DASH_SPEED_MULTIPLIER = 10;
    public static final int DASH_PARTICLE_TICKS = 20 * 3;

    public DashAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 10);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        sv.user.setVelocity(ctx.inputDir().multiply(sv.getMovementSpeed() * DASH_SPEED_MULTIPLIER).withAxis(Direction.Axis.Y, 0));
        sv.effectsManager().spawnRing(sv.user.getPos(), ctx.inputDir());
        sv.stateManager().enableStateFor(ParticleTrailEffectState.ID, DASH_PARTICLE_TICKS);
        MovementType.NEUTRAL.travel(sv, ctx);
    }
}
