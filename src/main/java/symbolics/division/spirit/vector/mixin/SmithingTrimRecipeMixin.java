package symbolics.division.spirit.vector.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit.vector.SpiritVectorItems;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin implements SmithingRecipe {
    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack svStack = input.base().copyWithCount(1);
        AbilitySlot slot = input.template().get(AbilitySlot.COMPONENT);
        SpiritVectorAbility ability = input.addition().get(SpiritVectorAbility.COMPONENT);
        if (slot != null && ability != null && svStack.isOf(SpiritVectorItems.SPIRIT_VECTOR)) {
            // h
            SpiritVectorHeldAbilities held = new SpiritVectorHeldAbilities(svStack.getOrDefault(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities()));
            held.set(slot, ability);
            svStack.set(SpiritVectorHeldAbilities.COMPONENT, held);
            ci.setReturnValue(svStack);
        }
    }
}
