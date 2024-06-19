package symbolics.division.spirit.vector.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import symbolics.division.spirit.vector.logic.ability.SpiritVectorHeldAbilities;

public class SpiritVectorItem extends ArmorItem {
    public SpiritVectorItem() {
        super(
                ArmorMaterials.DIAMOND,
                Type.BOOTS,
                new Settings().maxDamage(Type.BOOTS.getMaxDamage(33))
                        .component(SpiritVectorHeldAbilities.COMPONENT, new SpiritVectorHeldAbilities())
        );
    }
}
