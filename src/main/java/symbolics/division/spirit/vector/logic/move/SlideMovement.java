package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.MovementContext;
import symbolics.division.spirit.vector.logic.SVMathHelper;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.state.ParticleTrailEffectState;

public class SlideMovement extends BaseMovement  {

    
    public SlideMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, MovementContext ctx) {
        return sv.user.isSneaking() && !ctx.jumping && sv.user.isOnGround();
    }

    @Override
    public void travel(SpiritVector sv, MovementContext ctx) {
        // add DI for rotating slide
        Vec3d input = SVMathHelper.movementInputToVelocity(ctx.input, 1, sv.user.getYaw());
        Vec3d vel = new Vec3d(sv.user.getVelocity().x, -0.001, sv.user.getVelocity().z);
        double speed = vel.length();
        Vec3d side = vel.crossProduct(new Vec3d(0, 1, 0)).normalize();
        sv.user.setVelocity(vel.add(side.multiply(side.dotProduct(input) / 10)).normalize().multiply(speed));

        sv.user.move(net.minecraft.entity.MovementType.SELF, sv.user.getVelocity());
        sv.getStateManager().enableStateFor(ParticleTrailEffectState.ID, 1);

        ctx.ci.cancel();
    }

    @Override
    public void updateValues(SpiritVector sv) {}
}
