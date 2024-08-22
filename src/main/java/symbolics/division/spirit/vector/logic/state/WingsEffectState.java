package symbolics.division.spirit.vector.logic.state;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.vector.SpiritVector;

public class WingsEffectState extends ManagedState {

    public static final Identifier ID = SpiritVectorMod.id("wings");

    public WingsEffectState(SpiritVector sv) {
        super(sv);
    }

    protected boolean active;
    public static final ComponentType<Boolean> SPIRIT_WINGS_VISIBLE = ComponentType.<Boolean>builder()
            .codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();

    @Override
    public void tick() {
        updateWingState(true);
        super.tick();
    }

    @Override
    public void tickInactive() {
        updateWingState(false);
        super.tickInactive();
    }

    protected void updateWingState(boolean current) {
        if (active == current) return;
        active = current;
        // don't use data tracker, it sucks apparently
        if (sv.user instanceof ISpiritVectorUser svUser) {
            // set and synch with network
            svUser.setWingState(active);
        }

    }
}
