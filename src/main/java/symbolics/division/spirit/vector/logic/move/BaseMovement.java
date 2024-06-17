package symbolics.division.spirit.vector.logic.move;

import net.minecraft.entity.Flutterer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.MovementContext;
import symbolics.division.spirit.vector.logic.MovementType;
import symbolics.division.spirit.vector.logic.SpiritVector;

public class BaseMovement implements MovementType {
    protected final Identifier id;

    public BaseMovement(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, MovementContext ctx) {
        return false;
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, MovementContext ctx) {
        return true;
    }

    @Override
    public void travel(SpiritVector sv, MovementContext ctx) {
        // [vanillacopy]
        var user = sv.user;

        // prevent air input
        var input = ctx.input;
        var v = user.getVelocity();
        var horizontalSpeed = v.x*v.x + v.z*v.z;
        if (!user.isOnGround()) {
            input = new Vec3d(0, 0, 0);
            user.setNoDrag(true);
        } else {
            user.setNoDrag(false);
        }

        double d = user.getFinalGravity();
        boolean bl = user.getVelocity().y <= 0.0;
        if (bl && user.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            d = Math.min(d, 0.01);
        }

        BlockPos blockPos = user.getVelocityAffectingPos();
        float slip = user.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
        float baseFriction = 0.91f;
        float f = baseFriction * slip; // user.isOnGround() ? baseFriction * frictionModifier : baseFriction;

        Vec3d vec3d6 = user.applyMovementInput(input, slip);
        double q = vec3d6.y;
        if (user.hasStatusEffect(StatusEffects.LEVITATION)) {
            q += (0.05 * (double)(user.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2;
        } else if (!user.getWorld().isClient || user.getWorld().isChunkLoaded(blockPos)) {
            q -= d;
        } else if (user.getY() > (double)user.getWorld().getBottomY()) {
            q = -0.1;
        } else {
            q = 0.0;
        }

        if (user.hasNoDrag()) {
            user.setVelocity(vec3d6.x, q, vec3d6.z);
        } else {
            user.setVelocity(vec3d6.x * (double)f, user instanceof Flutterer ? q * (double)f : q * 0.98F, vec3d6.z * (double)f);
        }

        user.updateLimbs(user instanceof Flutterer);
        ctx.ci.cancel();
    }

    @Override
    public void updateValues(SpiritVector sv) {
        // by default, momentum decays over time
        if (sv.user.age % 20 == 0) {
            sv.modifyMomentum(-1);
        }
    }

}
