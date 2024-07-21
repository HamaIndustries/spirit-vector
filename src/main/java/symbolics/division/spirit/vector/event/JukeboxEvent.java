package symbolics.division.spirit.vector.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public final class JukeboxEvent {
    private JukeboxEvent() {}

    public static final Event<JukeboxEvent.Play> PLAY = EventFactory.createArrayBacked(JukeboxEvent.Play.class, callbacks -> (world, manager, pos) -> {
        for (JukeboxEvent.Play callback : callbacks) {
            callback.play(world, manager, pos);
        }
    });

    @FunctionalInterface
    public interface Play {
        void play(WorldAccess world, JukeboxManager manager, BlockPos pos);
    }
}
