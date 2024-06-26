package symbolics.division.spirit.vector.sfx;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import symbolics.division.spirit.vector.logic.move.MovementUtils;

public class ModelController {
    public static void controlLimbs(LivingEntity entity, ModelPart leftLeg, ModelPart rightLeg, ModelPart leftArm, ModelPart rightArm) {
        if (entity.isInPose(EntityPose.CROUCHING) && (
                entity.isOnGround()
                || (MovementUtils.idealWallrunningConditions(entity.getWorld(), entity.getBlockPos()))
                )) {
            float sway = (float)Math.sin(entity.age / 20f) * 0.1f;
            rightLeg.pitch = -0.6f;
            leftLeg.pitch = 0.6F;
            rightArm.pitch = 0;
            rightArm.roll = 0.5F + sway;
            leftArm.pitch = 0;
            leftArm.roll = -0.5F - sway;
        }
    }
}
