package symbolics.division.spirit_vector.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.spirit_vector.SpiritVectorItems;
import symbolics.division.spirit_vector.SpiritVectorTags;
import symbolics.division.spirit_vector.logic.ability.AbilitySlot;
import symbolics.division.spirit_vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit_vector.logic.ability.SpiritVectorHeldAbilities;
import symbolics.division.spirit_vector.sfx.SFXPack;
import symbolics.division.spirit_vector.sfx.SFXRegistry;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin implements SmithingRecipe {
    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack svStack = input.base().copyWithCount(1);

        RegistryEntry<SFXPack<?>> entry = input.template().get(SFXPack.COMPONENT);
        if (entry != null && input.addition().isIn(SpiritVectorTags.Items.SFX_PACK_ADDITIONS)) {
            var pack = entry.value();
            svStack.set(SFXPack.COMPONENT, SFXRegistry.entryOf(pack));
            ci.setReturnValue(svStack);
        }

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
