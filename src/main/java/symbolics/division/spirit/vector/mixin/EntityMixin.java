package symbolics.division.spirit.vector.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.move.LedgeVaultMovement;
import symbolics.division.spirit.vector.logic.move.MovementUtils;

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
    private static float VAULT_TRIGGER_STEP_DISTANCE = 0.3f;

    // tells the "physics system" (lol) that we're on the ground for the purpose of ledging
    @WrapOperation(
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnGround()Z")
    )
    public boolean youCanTellByTheWayIUseMyWalk(Entity instance, Operation<Boolean> value) {
        if (((Entity)(Object)this) instanceof LivingEntity entity && SpiritVector.hasEquipped(entity)) {
            return true;
        } else {
            return value.call(instance);
        }
    }

    @ModifyReturnValue( // chainable with others, if they exist
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At(value = "RETURN", ordinal = 0) // only for step-ups
    )
    public Vec3d imAWomansManNoTimeToTalk(Vec3d result, @Local(ordinal = 0) Vec3d movement) {
        Entity entity = ((Entity)(Object)this);
        double stepAdjust = result.y - movement.y;
        if ((  ( stepAdjust >= VAULT_TRIGGER_STEP_DISTANCE && entity.isOnGround() )
            || ( stepAdjust > 0 && !entity.isOnGround()) )
            && entity instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(LedgeVaultMovement::triggerLedge);
        }
        return result;
    }

    @ModifyReturnValue(
        method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
        at = @At("TAIL") // only non-step-ups
    )
    public Vec3d stepDown(Vec3d result, @Local(ordinal = 0) Vec3d movement) {
        Entity entity = (Entity)(Object)this;
        if ( result.y <= 0.0
             && result.x != 0.0
             && result.z != 0.0
             && entity.isOnGround()
             && entity instanceof ISpiritVectorUser user
             && user.spiritVector() != null) {
            // try to make movement go down as far as possible
            return MovementUtils.stepDown(entity, result, movement);
        }
        return result;
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
            target = "Lnet/minecraft/entity/Entity;hasCollidedSoftly(Lnet/minecraft/util/math/Vec3d;)Z"
        )
    )
    public void triggerWallVault(MovementType type, Vec3d move, CallbackInfo ci) {
        if (this instanceof ISpiritVectorUser user) {
            user.getSpiritVector().ifPresent(LedgeVaultMovement::triggerLedge);
        }
    }
}
