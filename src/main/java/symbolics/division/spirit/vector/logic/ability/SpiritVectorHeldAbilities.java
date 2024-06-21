package symbolics.division.spirit.vector.logic.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

/*
controller for all of a particular sv's abilities
 */
public class SpiritVectorHeldAbilities {

    private SpiritVectorAbility[] abilities = {
       SpiritVectorAbility.NONE, SpiritVectorAbility.NONE, SpiritVectorAbility.NONE
    };

    public static final Codec<SpiritVectorHeldAbilities> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    SpiritVectorAbility.CODEC.fieldOf("left").forGetter(sva -> sva.get(AbilitySlot.LEFT)),
                    SpiritVectorAbility.CODEC.fieldOf("up").forGetter(sva -> sva.get(AbilitySlot.UP)),
                    SpiritVectorAbility.CODEC.fieldOf("right").forGetter(sva -> sva.get(AbilitySlot.RIGHT))
            ).apply(instance, SpiritVectorHeldAbilities::new)
    );

    public static final PacketCodec<RegistryByteBuf, SpiritVectorHeldAbilities> PACKET_CODEC = PacketCodec.tuple(
            SpiritVectorAbility.PACKET_CODEC,
            sva -> sva.get(AbilitySlot.LEFT),
            SpiritVectorAbility.PACKET_CODEC,
            sva -> sva.get(AbilitySlot.UP),
            SpiritVectorAbility.PACKET_CODEC,
            sva -> sva.get(AbilitySlot.RIGHT),
            SpiritVectorHeldAbilities::new
    );

    public static final ComponentType<SpiritVectorHeldAbilities> COMPONENT = ComponentType.<SpiritVectorHeldAbilities>builder()
            .codec(CODEC)
            .packetCodec(PACKET_CODEC)
            .build();

    public SpiritVectorHeldAbilities() {}
    public SpiritVectorHeldAbilities(SpiritVectorHeldAbilities other) {
        this(other.get(AbilitySlot.LEFT), other.get(AbilitySlot.UP), other.get(AbilitySlot.RIGHT));
    }
    public SpiritVectorHeldAbilities(SpiritVectorAbility left, SpiritVectorAbility up, SpiritVectorAbility right) {
        set(AbilitySlot.LEFT, left);
        set(AbilitySlot.UP, up);
        set(AbilitySlot.RIGHT, right);
    }

    private int getIndex(AbilitySlot slot) {
        return switch (slot) {
            case LEFT -> 0;
            case UP -> 1;
            case RIGHT -> 2;
        };
    }

    public SpiritVectorAbility get(AbilitySlot slot) {
        return abilities[getIndex(slot)];
    }

    public void set(AbilitySlot slot, SpiritVectorAbility ability) {
        abilities[getIndex(slot)] = ability;
    }

    public SpiritVectorAbility[] getAll() { return abilities.clone(); }
}
