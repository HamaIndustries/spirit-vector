package symbolics.division.spirit.vector.logic.input;

import java.util.HashMap;
import java.util.Map;

public final class InputManager {

    /*
    Inputs MUST be consumed at the end of all logic chains testing for activation.
    This means that if an input is consumed, it MUST produce a user-facing action.

    In addition, inputs should always be consumed if they are expected to be the primary
    means of firing some event that expects to be exclusive to that input.

    maybe we should make this an event-based thing rather than state. /shrug

    its like 1 am hopefully this means something
     */

    private final Map<Input, Boolean> trackedStates = new HashMap<>();
    private final Map<Input, Boolean> publicStates = new HashMap<>();

    {
        for (Input input : Input.values()) {
            trackedStates.put(input, false);
            publicStates.put(input, false);
        }
    }

    public boolean consume(Input input) {
        var result = publicStates.get(input);
        if (result) {
            publicStates.put(input, false);
        }
        return result;
    }

    public boolean pressed(Input input) { // check absolute state without consuming
        return trackedStates.get(input);
    }

    public void update(Input input, boolean value) {
        if (trackedStates.get(input) != value) {
            trackedStates.put(input, value);
            publicStates.put(input, value);
        }
    }

    // mainly for debug, see whether input is consumed
    public boolean trackedState(Input input) {
        return publicStates.get(input);
    }
}
