package symbolics.division.spirit_vector.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;
import symbolics.division.spirit_vector.sfx.ModelController;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
    @Shadow private ModelPart rightLeg;
    @Shadow private ModelPart leftLeg;
    @Shadow private ModelPart rightArm;
    @Shadow private ModelPart leftArm;

    @Inject(method = "setAngles", at =@At("TAIL"))
    public void setAngles(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (SpiritVector.hasEquipped(entity)) {
            ModelController.controlLimbs(entity, leftLeg, rightLeg, leftArm, rightArm);
        }
    }
}
