package symbolics.division.spirit_vector.logic;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public record TravelMovementContext(Vec3d input, CallbackInfo ci, Vec3d inputDir) {
    // note: inputDir is always normalized to 1 magnitude
}
