package symbolics.division.spirit.vector.item;

import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import symbolics.division.spirit.vector.sfx.SFXPack;
import symbolics.division.spirit.vector.sfx.SFXRegistry;
import symbolics.division.spirit.vector.sfx.SpiritVectorSFX;

// anima core
public class SFXContainerItem extends Item {

    public SFXContainerItem() {
        super(new Item.Settings().component(SFXPack.COMPONENT, RegistryEntry.of(SpiritVectorSFX.getDefault())));
    }
}
