package symbolics.division.spirit_vector.logic.state;

import net.minecraft.util.Identifier;

import java.util.*;

// statemanagers and managedstates are all unique to each sv
public final class StateManager {

    private final Map<Identifier, IManagedState> states = new HashMap<>();

    public void register(Identifier id, IManagedState state) {
        if (states.getOrDefault(id, state) != state) {
            throw new RuntimeException("Attempted to register state to occupied id");
        }
        states.put(id, state);
    }

    public void enableStateFor(Identifier id, int ticks) {
        getState(id).enableFor(ticks);
    }

    public void enableState(Identifier id) {
        getState(id).enable();
    }

    public void disableState(Identifier id) {
        getState(id).disable();
    }

    public void clearTicks(Identifier id) {
        getState(id).clearTicks();
    }

    public void tick() {
        for (IManagedState state : states.values()) {
            if (state.isActive()) {
                state.tick();
            } else {
                state.tickInactive();
            }
        }
    }

    public IManagedState getState(Identifier id) {
        var state = states.get(id);
        if (state == null) {
            throw new RuntimeException("Tried to get unregistered state with id " + id);
        }
        return state;
    }

    public boolean isActive(Identifier id) {
        return getState(id).isActive();
    }
    public Optional<IManagedState> getOptional(Identifier id) {
        return Optional.ofNullable(states.get(id));
    }
}
