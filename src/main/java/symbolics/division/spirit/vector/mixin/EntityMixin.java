package symbolics.division.spirit.vector.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.move.LedgeVaultMovement;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {

    // so that we are completely clear:

    // adjustMovementForCollisions(Vec3d)
    // aka Entity.collide
    // main """physics""" collision calc containing block collision
    // and step-up logic. wraps the other two.

    // adjustMovementForCollisions(Vec3d, Box, List<VoxelShape>)
    // aka Entity.collideWithShapes
    // takes a movement vector, a bounding box and a list of shapes,
    // and determines how far within that box you can move without hitting a shape
    // then returns the clipped vector

    // adjustMovementForCollisions(@Nullable Entity, Vec3d, Box, World, List<VoxelShape>)
    // aka Entity.collideBoundingBox
    // takes an entity, movement and the entity's bounding box, adds block collisions
    // to the list of provided shapes, then returns movement vector clipped
    // to not pass those shapes.
    // the list is usually a list of special collisions for entities provided by the world.

    // findCollisionsForMovement(@Nullable Entity, World, List<VoxelShape>, Box)
    // aka Entity.collectColliders
    // produces a list of collision shapes combining an input list with
    // the worldborder hitbox and block collision hitboxes

    // the convoluted stepheight logic is to find the furthest it can travel horizontally
    // along its desired movement where a step up can occur

    // slabs count as 1.0 for some reason, whatever
    private static final float VAULT_TRIGGER_STEP_DISTANCE = 0.3f;

    @Shadow // collectColliders
    static List<VoxelShape> findCollisionsForMovement(
            @Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox
    ){ throw new NotImplementedException("mixin access failed"); }

    @Shadow // collideWithShapes
    static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        throw new NotImplementedException("mixin access failed :{");
    }

    @Shadow
    static float[] collectStepHeights(Box collisionBox, List<VoxelShape> collisions, float maxStepHeight, float movementMaxStepHeight) {
        throw new NotImplementedException("mixin access failed");
    }

    @Inject(method = "onLanding", at = @At("HEAD"))
    public void onLanding(CallbackInfo ci) {
        if (this instanceof ISpiritVectorUser user) {
            // fake reset for chain jumps
            user.getSpiritVector().ifPresent(SpiritVector::onLanding);
        }
    }

    @Inject(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;hasCollidedSoftly(Lnet/minecraft/util/math/Vec3d;)Z"))
    public void triggerWallVault(MovementType type, Vec3d move, CallbackInfo ci) {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().filter(sv -> sv.user.isOnGround()).ifPresent(LedgeVaultMovement::triggerLedge);
        }
    }

    @Inject(
            method = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> ci) {
        if (!(this instanceof ISpiritVectorUser user) || user.spiritVector() == null) return;
        LivingEntity entity = user.spiritVector().user;
        float stepHeight = entity.getStepHeight();
        Box entityBoundingBox = entity.getBoundingBox();
        // get collisions with other entities
        List<VoxelShape> entityCollisions = entity.getWorld().getEntityCollisions(entity, entityBoundingBox.stretch(movement));
        // Entity.collideBoundingBox
        // add block and worldborder collisions, clip movement vector
        Vec3d adjustedMovement = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(entity, movement, entityBoundingBox, entity.getWorld(), entityCollisions);
        boolean adjX = movement.x != adjustedMovement.x;
        boolean adjY = movement.y != adjustedMovement.y;
        boolean adjZ = movement.z != adjustedMovement.z;
        boolean adjYDownwards = adjY && movement.y < 0.0;
        if (stepHeight > 0.0F && (adjX || adjZ)) {
            Box downwardsOffsetEntityBB = adjYDownwards ? entityBoundingBox.offset(0.0, adjustedMovement.y, 0.0) : entityBoundingBox;
            Box entityMovementBB = downwardsOffsetEntityBB.stretch(movement.x, (double)stepHeight, movement.z);
            if (!adjYDownwards) {
                // Adds a little to the bottom to ensure contact with ground is considered in step-up
                entityMovementBB = entityMovementBB.stretch(0.0, -1.0E-5F, 0.0);
            }

            // collect possible blocks we can step up onto
            List<VoxelShape> list2 = findCollisionsForMovement(entity, entity.getWorld(), entityCollisions, entityMovementBB);
            float adjustedMovementY = (float)adjustedMovement.y;
            float[] stepHeights = collectStepHeights(downwardsOffsetEntityBB, list2, stepHeight, adjustedMovementY);

            Vec3d stepUpMovement = adjustedMovement;
            boolean stepUpOccurred = false;
            for (float stepHeightCandidate : stepHeights) {
                Vec3d candidateStepUpMovement = adjustMovementForCollisions(new Vec3d(movement.x, (double)stepHeightCandidate, movement.z), downwardsOffsetEntityBB, list2);
                if (candidateStepUpMovement.horizontalLengthSquared() > adjustedMovement.horizontalLengthSquared()) {
                    stepUpMovement = candidateStepUpMovement;
                    stepUpOccurred = true;
                }
            }

            if (stepUpOccurred) {
                double d = entityBoundingBox.minY - downwardsOffsetEntityBB.minY;
                stepUpMovement = stepUpMovement.add(0.0, -d, 0.0);

                double stepAdjust = stepUpMovement.y - movement.y;
                if (       ( stepAdjust >= VAULT_TRIGGER_STEP_DISTANCE && entity.isOnGround() )
                        || ( stepAdjust > 0 && !entity.isOnGround()) ) {
                    user.getSpiritVector().ifPresent(LedgeVaultMovement::triggerLedge);
                }
//                    analyze(result);
                ci.setReturnValue(stepUpMovement);
                ci.cancel();
                return;
            }
        }

        if (       adjustedMovement.y <= 0.0
                && adjustedMovement.x != 0.0
                && adjustedMovement.z != 0.0
                && entity.isOnGround()) {
            Vec3d stepDownAdjustedMovement = stepDown(entity, adjustedMovement);
            var stepDownResultPos = entity.getPos().add(stepDownAdjustedMovement);
            if (entity.getWorld().getBlockState(BlockPos.ofFloored(stepDownResultPos)).getFluidState().isEmpty()) {
                adjustedMovement = stepDownAdjustedMovement;
            }
        }

        ci.setReturnValue(adjustedMovement);
        ci.cancel();
    }

    private static Vec3d stepDown(Entity entity, Vec3d result) {
        // hypothetical hitbox of entity after movement is applied
        Box hypothetical = entity.getBoundingBox().offset(result);
        // drop bottom by step height
        Box down = hypothetical.stretch(0, -entity.getStepHeight(), 0);
        // get collisions such as with other entities
        // todo remove this, use the one from the method
        List<VoxelShape> specialCollisions = entity.getWorld().getEntityCollisions(entity, down);
        // add world border and block collisions
        List<VoxelShape> collisions = findCollisionsForMovement(entity, entity.getWorld(), specialCollisions, down);

        double stepdown = adjustMovementForCollisions(
                new Vec3d(0, -entity.getStepHeight() - 0.001, 0), // add a little bit to filter air movement
                hypothetical,
                collisions
        ).y;

        //don't step into open air
        if (stepdown >= -entity.getStepHeight()) {
            return result.add(0, stepdown, 0);
        }
        return result;
    }
}
