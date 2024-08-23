package symbolics.division.spirit_vector.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
import symbolics.division.spirit_vector.logic.ISpiritVectorUser;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;
import symbolics.division.spirit_vector.logic.move.LedgeVaultMovement;

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

    @Shadow private boolean collidedSoftly;

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
    public void triggerWallVault(MovementType type, Vec3d move, CallbackInfo ci, @Local(ordinal = 0) boolean xBlocked, @Local(ordinal = 1) boolean zBlocked) {
        if (this instanceof ISpiritVectorUser user && user.spiritVector() != null) {
            SpiritVector sv = user.spiritVector();
            if (sv.user.isOnGround() &&
                    ((xBlocked && (sv.user.getX() != sv.user.prevX))
                  || (zBlocked && (sv.user.getZ() != sv.user.prevZ)))) {
                LedgeVaultMovement.triggerLedge(sv);
            }
        }
    }

    @Inject(
            method = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void adjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> ci) {
        if (!(this instanceof ISpiritVectorUser user)) return;
        SpiritVector sv = user.spiritVector();
        if (sv == null) return;
        LivingEntity entity = sv.user;
        float stepHeight = entity.getStepHeight();
        Box entityBoundingBox = entity.getBoundingBox();

        // get collisions with other entities
        List<VoxelShape> entityCollisions = entity.getWorld().getEntityCollisions(entity, entityBoundingBox.stretch(movement));

        // Entity.collideBoundingBox
        // add block and worldborder collisions, clip movement vector
        Vec3d adjustedMovement = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(entity, movement, entityBoundingBox, entity.getWorld(), entityCollisions);

        Vec3d inputDir = sv.getInputDirection();
        boolean slideOver = false;
        if (
            // if flying upwards while sliding,
            movement.y > 0 && !entity.isOnGround() &&
            // and we are trying to move in a direction we are not currently moving,
            ((adjustedMovement.x == 0 && inputDir.x != 0) || (adjustedMovement.z == 0 && inputDir.z != 0))
        ) {
            // see if there's a wall stopping us from moving in that direction and attempt slideover if so.
            Vec3d influencedMovement = adjustedMovement.add(
                    adjustedMovement.x == 0 ? inputDir.x * 0.1 : 0,
                    0,
                    adjustedMovement.z == 0 ? inputDir.z * 0.1 : 0
            );
            Vec3d resultingMovement = Entity.adjustMovementForCollisions(entity, influencedMovement, entityBoundingBox, entity.getWorld(), entityCollisions);
            boolean influenceX = resultingMovement.x != influencedMovement.x;
            boolean influenceZ = resultingMovement.z != influencedMovement.z;
            slideOver = influenceX || influenceZ;
            movement = movement.add(
                    movement.x == 0 && influenceX ? inputDir.x * 0.1 : 0,
                    0,
                    movement.z == 0 && influenceZ ? inputDir.z * 0.1 : 0
            );
        }

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
                    LedgeVaultMovement.triggerLedge(sv);
                }
                if (slideOver) {
                    // convert upwards speed to horizontal
                    double speed = entity.getVelocity().length();
                    sv.setImpulse(stepUpMovement.withAxis(Direction.Axis.Y, 0).normalize().multiply(speed));
                }
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

    private static final double STEPDOWN_HEIGHT = 1.4;

    private static Vec3d stepDown(Entity entity, Vec3d result) {
        // hypothetical hitbox of entity after movement is applied
        Box hypothetical = entity.getBoundingBox().offset(result);
        // drop bottom by step height
        Box down = hypothetical.stretch(0, -STEPDOWN_HEIGHT, 0);
        // get collisions such as with other entities
        // todo remove this, use the one from the method
        List<VoxelShape> specialCollisions = entity.getWorld().getEntityCollisions(entity, down);
        // add world border and block collisions
        List<VoxelShape> collisions = findCollisionsForMovement(entity, entity.getWorld(), specialCollisions, down);

        double stepdown = adjustMovementForCollisions(
                new Vec3d(0, -STEPDOWN_HEIGHT - 0.001, 0), // add a little bit to filter air movement
                hypothetical,
                collisions
        ).y;

        //don't step into open air
        if (stepdown >= -STEPDOWN_HEIGHT) {
            return result.add(0, stepdown, 0);
        }
        return result;
    }
}
