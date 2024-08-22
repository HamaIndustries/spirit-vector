package symbolics.division.spirit.vector.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;
import symbolics.division.spirit.vector.logic.vector.VectorType;

import java.util.List;

public class SpiritVectorItem extends ArmorItem {
    public SpiritVectorItem() {
        super(
                ArmorMaterials.DIAMOND,
                Type.BOOTS,
                new Settings()
                        .component(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities())
                        .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(false))
                        .maxCount(1)
        );
    }

    @Override
    public Text getName(ItemStack stack) {
        VectorType type = stack.getOrDefault(VectorType.COMPONENT, RegistryEntry.of(VectorType.SPIRIT)).value();
        return SFXPackItem.applySFXToText(stack, this, Text.translatable("item.spirit_vector." + type.id()));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        var ab = stack.get(SpiritVectorHeldAbilities.COMPONENT);
        if (ab != null) {
            tooltip.add(Text.translatable("tooltip.spirit_vector.held_abilities").withColor(0x808080));
            tooltip.add(abilityText(ab, AbilitySlot.LEFT));
            tooltip.add(abilityText(ab, AbilitySlot.UP));
            tooltip.add(abilityText(ab, AbilitySlot.RIGHT));
        }
    }

    private MutableText abilityText(SpiritVectorHeldAbilities ab, AbilitySlot slot)  {
        // idk nobody like these
        return Text.literal("").withColor(0x808080)
                .append(Text.literal(slot.arrow).withColor(0xFFA500))
                .append(" [")
                .append(Text.keybind(slot.input.key).withColor(0xffffff))
                .append("] ")
                .append(Text.translatable(ab.get(slot).getMovement().getTranslationKey()).withColor(0xffffff));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            VectorType type = stack.getOrDefault(VectorType.COMPONENT, RegistryEntry.of(VectorType.SPIRIT)).value();
            int nextIndex = (VectorType.REGISTRY.getRawId(type) + 1) % VectorType.REGISTRY.size();
            stack.set(VectorType.COMPONENT, VectorType.REGISTRY.getEntry(nextIndex).orElseThrow());
            world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundCategory.PLAYERS, 1, 1);
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
    }
}
