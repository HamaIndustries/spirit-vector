package symbolics.division.spirit.vector.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;

public class SpiritVectorSkatesModel extends BipedEntityModel<LivingEntity> {

	public SpiritVectorSkatesModel(ModelPart root) {
		super(root);
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(new Dilation(1.2f), 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 3.0F, -2.0F, 4.0F, 9.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, 2.0F, 0.3927F, 0.0F, 0.0F));

		bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 8.0F, 2.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData bb_main2 = modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 3.0F, -2.0F, 4.0F, 9.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		bb_main2.addChild("cube_r1", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, 2.0F, 0.3927F, 0.0F, 0.0F));

		bb_main2.addChild("cube_r2", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 8.0F, 2.0F, 0.3927F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 32, 32);
	}

}