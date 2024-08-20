package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.ability.WaterRunAbility;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.state.ParticleTrailEffectState;

public class SlideMovement extends NeutralMovement {
    public static final float MIN_SPEED_FOR_TRAIL = 0.1f;

    public SlideMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean fluidMovementAllowed(SpiritVector sv) {
        return WaterRunAbility.canWaterRun(sv);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        // take raw input because we want to keep going after the first time
        if ((sv.user.isOnGround() || WaterRunAbility.canWaterRun(sv))
                && sv.inputManager().rawInput(Input.CROUCH)) {
            sv.inputManager().consume(Input.CROUCH);
            return true;
        }
        return false;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        // add DI for rotating slide
        Vec3d input = ctx.inputDir(); // non-augmented specifically (change ?)
        travelWithInput(sv, input);
        ctx.ci().cancel();
    }

    public static void travelWithInput(SpiritVector sv, Vec3d input) {
        double gravity = WaterRunAbility.isWaterRunning(sv) ? 0 : -0.001; // ensure set on ground
        Vec3d vel = new Vec3d(sv.user.getVelocity().x, gravity, sv.user.getVelocity().z);
        double speed = vel.length();
        Vec3d side = vel.crossProduct(new Vec3d(0, 1, 0)).normalize();
        sv.user.setVelocity(vel.add(side.multiply(side.dotProduct(input) / 10)).normalize().multiply(speed));

        sv.user.move(net.minecraft.entity.MovementType.SELF, sv.user.getVelocity());
        if (speed > MIN_SPEED_FOR_TRAIL) {
            sv.stateManager().enableStateFor(ParticleTrailEffectState.ID, 1);
        }
    }

    @Override
    public void updateValues(SpiritVector sv) {
        if (sv.user.age % 10 == 0 && WaterRunAbility.isWaterRunning(sv)) {
            sv.modifyMomentum(-1);
        }
    }
}
