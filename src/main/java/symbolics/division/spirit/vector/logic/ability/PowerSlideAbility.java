package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.skates.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.move.MovementType;
import symbolics.division.spirit.vector.logic.move.SlideMovement;
import symbolics.division.spirit.vector.logic.state.ManagedState;

public class PowerSlideAbility extends AbstractSpiritVectorAbility {
    private static final float COST_PER_SECOND = (float)SpiritVector.MAX_MOMENTUM / 10 / 2;
    private static final int TICKS_PER_COST = (int)(20 / COST_PER_SECOND);
    private static final Identifier SLIDING_STATE = SpiritVectorMod.id("power_slide_state");
    private static final float DIVE_SPEED = 2;

    public PowerSlideAbility(Identifier id) {
        super(id, 1);
    }

    @Override
    public void configure(SpiritVector sv) {
        super.configure(sv);
        sv.stateManager().register(SLIDING_STATE, new ManagedState(sv));
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        if (!sv.stateManager().isActive(SLIDING_STATE)) {
            sv.effectsManager().spawnRing(sv.user.getPos(), sv.user.getRotationVecClient());
            return true;
        } else if (sv.getMomentum() == 0) { //no juice :(
            sv.stateManager().disableState(SLIDING_STATE);
            return true;
        }
        return false;
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        sv.stateManager().enableState(SLIDING_STATE);
        return true;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        if (!sv.user.isOnGround()) {
            Vec3d diveVel = sv.user.getVelocity().withAxis(Direction.Axis.Y, 0).normalize().withAxis(Direction.Axis.Y, -DIVE_SPEED);
            sv.user.setVelocity(diveVel);
            MovementType.NEUTRAL.travel(sv, ctx);
        } else {
            SlideMovement.travelWithInput(sv, Vec3d.ZERO);
            var prevPos = new Vec3d(sv.user.prevX, sv.user.prevY, sv.user.prevZ);
            if (prevPos.distanceTo(sv.user.getPos()) < 0.0001){ // wall
                sv.stateManager().disableState(SLIDING_STATE);
                GroundPoundAbility.requestSlamEffect(sv, 120);
            }
            ctx.ci().cancel();
        }
    }

    @Override
    public void updateValues(SpiritVector sv) {
        if (sv.user.age % TICKS_PER_COST == 0) {
            sv.modifyMomentum(-1);
        }
    }

    @Override
    public boolean fluidMovementAllowed(SpiritVector sv) {
        return true;
    }
}
