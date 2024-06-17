package symbolics.division.spirit.vector.logic;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class MovementContext {
    public final Vec3d input;
    public final Vec3d inputDir;
    public final boolean jumping;
    public final CallbackInfo ci;

    public MovementContext(Vec3d input, boolean jumping, CallbackInfo ci, Vec3d inputDir) {
        this.input = input;
        this.jumping = jumping;
        this.ci = ci;
        this.inputDir = inputDir;
    }


}
