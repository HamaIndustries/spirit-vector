package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.move.MovementType;
import symbolics.division.spirit.vector.logic.state.ManagedState;

public class TransgenderAbility extends AbstractSpiritVectorAbility {
    private static final int COOLDOWN_TICKS = 10;
    private static final Identifier COOLDOWN_STATE = SpiritVectorMod.id("transgender_cooldown");

    public TransgenderAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 10);
    }

    @Override
    public void configure(SpiritVector sv) {
        super.configure(sv);
        sv.stateManager().register(COOLDOWN_STATE, new ManagedState(sv));
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return !sv.stateManager().isActive(COOLDOWN_STATE);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        // flatten our movement
        if (ctx.inputDir().lengthSquared() > 0.00001) {
            double hSpeed = sv.user.getVelocity().withAxis(Direction.Axis.Y, 0).length();
            sv.user.setVelocity(ctx.inputDir().multiply(hSpeed));
        } else {
            sv.user.setVelocity(sv.user.getVelocity().withAxis(Direction.Axis.Y, 0));
        }
        MovementType.JUMP.travel(sv, ctx);
        sv.effectsManager().spawnRing(sv.user.getPos(), sv.user.getRotationVecClient());
        sv.stateManager().enableStateFor(COOLDOWN_STATE, COOLDOWN_TICKS);
    }
}
