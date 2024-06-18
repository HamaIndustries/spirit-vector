package symbolics.division.spirit.vector.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.ability.AbilitySlot;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbilitiesRegistry;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorAbility;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;

public class SpiritVectorItem extends ArmorItem {
    public static String ID = "spirit_vector";
    public SpiritVectorItem() {
        super(
                ArmorMaterials.DIAMOND,
                Type.BOOTS,
                new Item.Settings().maxDamage(Type.BOOTS.getMaxDamage(33))
                        .component(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities())
        );
    }
}
