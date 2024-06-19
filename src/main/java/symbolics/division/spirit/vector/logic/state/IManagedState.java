package symbolics.division.spirit.vector.logic.state;

public interface IManagedState {
    void enable();
    void disable();
    void enableFor(int ticks);
    void tick();
    default void tickInactive() {}
    boolean isActive();
}
