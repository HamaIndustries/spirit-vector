package symbolics.division.spirit_vector.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Inject(
            method = "getRightText",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;targetedEntity:Lnet/minecraft/entity/Entity;")
    )
    public void injectItemTagsList(CallbackInfoReturnable<List<String>> ci, @Local(ordinal = 0) List<String> list) {
        if (this.client.player != null) {
            ItemStack stack = this.client.player.getMainHandStack();
            if (stack.isEmpty()) return;
            list.add("");
            list.add(Formatting.UNDERLINE + "Held Item");
            list.add(String.valueOf(Registries.ITEM.getId(stack.getItem())));
            stack.streamTags().map(tag -> "#" + tag.id()).forEach(list::add);
        }
    }
}
