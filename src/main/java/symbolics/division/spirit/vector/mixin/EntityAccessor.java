package symbolics.division.spirit.vector.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("adjustMovementForCollisions")
    static Vec3d invokeCollideWithShapes(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        throw new NotImplementedException("mixin :{{{");
    }

    @Invoker("findCollisionsForMovement")
    static List<VoxelShape> invokeCollectColliders(
            @Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox
    ){ throw new NotImplementedException("mixin access failed"); }

    @Invoker("collectStepHeights")
    static float[] invokeCollectStepHeights(Box collisionBox, List<VoxelShape> collisions, float maxStepHeight, float movementMaxStepHeight) {
        throw new NotImplementedException("mixin access failed");
    }
}
