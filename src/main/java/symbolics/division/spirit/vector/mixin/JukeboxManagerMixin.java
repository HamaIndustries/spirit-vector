package symbolics.division.spirit.vector.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.spirit.vector.event.JukeboxEvent;

@Mixin(JukeboxManager.class)
public class JukeboxManagerMixin {
    @WrapOperation(
            method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/event/GameEvent$Emitter;)V")
    )
    private void injectJukeboxPlayEvent(WorldAccess world, RegistryEntry<GameEvent> event, BlockPos pos, GameEvent.Emitter emitter, Operation<Void> op) {
        JukeboxEvent.PLAY.invoker().play(world, (JukeboxManager)(Object)this, pos);
        op.call(world, event, pos, emitter);
    }
}
