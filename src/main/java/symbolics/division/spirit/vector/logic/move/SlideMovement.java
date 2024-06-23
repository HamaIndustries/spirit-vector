package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.SVMathHelper;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.state.ParticleTrailEffectState;
import symbolics.division.spirit.vector.mixin.LivingEntityAccessor;

public class SlideMovement extends GroundMovement {
    public static final float MIN_SPEED_FOR_TRAIL = 0.1f;

    public SlideMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        // take raw input because we want to keep going after the first time
        if (sv.user.isOnGround() && sv.inputManager().rawInput(Input.CROUCH)) {
            sv.inputManager().consume(Input.CROUCH);
            return true;
        }
        return false;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        // add DI for rotating slide
        Vec3d input = SVMathHelper.movementInputToVelocity(ctx.input(), 1, sv.user.getYaw());
        travelWithInput(sv, input);
        ctx.ci().cancel();
    }

    public static void travelWithInput(SpiritVector sv, Vec3d input) {
        Vec3d vel = new Vec3d(sv.user.getVelocity().x, -0.001, sv.user.getVelocity().z);
        double speed = vel.length();
        Vec3d side = vel.crossProduct(new Vec3d(0, 1, 0)).normalize();
        sv.user.setVelocity(vel.add(side.multiply(side.dotProduct(input) / 10)).normalize().multiply(speed));

        sv.user.move(net.minecraft.entity.MovementType.SELF, sv.user.getVelocity());
        if (speed > MIN_SPEED_FOR_TRAIL) {
            sv.stateManager().enableStateFor(ParticleTrailEffectState.ID, 1);
        }
    }

    @Override
    public void updateValues(SpiritVector sv) {}
}
