package symbolics.division.spirit.vector.logic.move;

import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.JumpMovementContext;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.mixin.LivingEntityAccessor;

public class BaseMovement extends AbstractMovementType {

    public BaseMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return false;
    }

    protected static boolean isCreativeFlying(SpiritVector sv) {
        return sv.user instanceof PlayerEntity player && player.getAbilities().flying;
    }

    @Override
    public boolean disableDrag(SpiritVector sv) {
        return !sv.user.isOnGround() && !isCreativeFlying(sv);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        if (isCreativeFlying(sv)) return;
        // [vanillacopy] LivingEntity.travel
        LivingEntity user = sv.user;

        // prevent air input
        Vec3d input = ctx.input();
        if (!user.isOnGround()) {
            input = new Vec3d(0, 0, 0);
        }

        double gravity = user.getFinalGravity();
        boolean movingDownwards = user.getVelocity().y <= 0.0;
        if (movingDownwards && user.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            gravity = Math.min(gravity, 0.01);
        }

        // normal slip 0.6
        // ice slip 0.96
        // swap to ice slip when on ground and no input
        BlockPos blockPos = user.getVelocityAffectingPos();
        float normal_slip = user.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
        float ice_slip = 0.96f;
        float slip = input.x*input.x+input.z*input.z > 0 || !user.isOnGround() ? normal_slip : ice_slip;
        float friction = user.isOnGround() ? slip * 0.91F : 0.91F; // magic drag number
        Vec3d appliedVelocity = user.applyMovementInput(input, slip);
        double velocityY = appliedVelocity.y;
        if (user.hasStatusEffect(StatusEffects.LEVITATION)) {
            velocityY += (0.05 * (double)(user.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - appliedVelocity.y) * 0.2;
        } else if (!user.getWorld().isClient || user.getWorld().isChunkLoaded(blockPos)) {
            velocityY -= gravity;
        } else if (user.getY() > (double)user.getWorld().getBottomY()) {
            velocityY = -0.1;
        } else {
            velocityY = 0.0;
        }

        if (user.hasNoDrag()) {
            user.setVelocity(appliedVelocity.x, velocityY, appliedVelocity.z);
        } else {
            user.setVelocity(appliedVelocity.x * (double)friction, this instanceof Flutterer ? velocityY * (double)friction : velocityY * 0.98F, appliedVelocity.z * (double)friction);
        }

        user.updateLimbs(user instanceof Flutterer);
        ctx.ci().cancel();
    }

    @Override
    public void jump(SpiritVector sv, JumpMovementContext ctx) {
        float f = ((LivingEntityAccessor)sv.user).callGetJumpVelocity() * 1.2f;
        if (f <= 0.00001) return;
        sv.user.addVelocity(0, f, 0);
        sv.user.velocityDirty = true;
        ctx.ci().cancel();
    }

    @Override
    public void exit(SpiritVector sv) {
        sv.user.setNoDrag(false);
    }

    @Override
    public void updateValues(SpiritVector sv) {
        // by default, momentum decays over time
        if (sv.user.age % 20 == 0) {
            sv.modifyMomentum(-1);
        }
    }
}
