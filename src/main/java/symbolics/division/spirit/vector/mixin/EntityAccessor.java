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
    Vec3d invokeAdjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions);

    @Invoker("findCollisionsForMovement")
    static List<VoxelShape> invokeFindCollisionsForMovement(
            @Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox
    ){ throw new NotImplementedException("mixin access failed"); }
}
