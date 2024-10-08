package symbolics.division.spirit_vector.logic.state;

public interface IManagedState {
    void enable();
    void disable();
    void enableFor(int ticks);
    void clearTicks();
    void tick();
    default void tickInactive() {}
    boolean isActive();
    int ticksLeft();
}
