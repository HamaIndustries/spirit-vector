package symbolics.division.spirit.vector.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;

public class SpiritVectorItem extends ArmorItem {
    public static String ID = "spirit_vector";
    public SpiritVectorItem() {
        super(
                ArmorMaterials.DIAMOND,
                Type.BOOTS,
                new Item.Settings().maxDamage(Type.BOOTS.getMaxDamage(33))
        );
    }
}
