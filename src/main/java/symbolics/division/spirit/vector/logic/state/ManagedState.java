package symbolics.division.spirit.vector.logic.state;

import symbolics.division.spirit.vector.logic.vector.SpiritVector;

// manages state of visual effects
public class ManagedState implements IManagedState {
    protected int semaphore = 0;
    protected int ticksLeft = 0;
    protected final SpiritVector sv;

    public ManagedState(SpiritVector sv) {
        this.sv = sv;
    }

    @Override
    public void enable() {
        semaphore++;
    }

    @Override
    public void enableFor(int ticks) {
        ticksLeft = Math.max(ticks, ticksLeft);
    }

    @Override
    public void disable() {
        semaphore--;
        if (semaphore < 0) {
            // if someone tries to disable without enabling first, someone messed up in the chain
            // we don't want to perpetuate bad state, so...
            throw new RuntimeException("Attempted to disable effect state while not enabled");
        }
    }

    @Override
    public boolean isActive() {
        return ticksLeft > 0 || semaphore > 0;
    }

    @Override
    public void tick() {
        if (ticksLeft > 0) {
            ticksLeft--;
        }
    }

    @Override
    public void clearTicks() {
        ticksLeft = 0;
    }

    @Override
    public int ticksLeft() {
        return ticksLeft;
    }
}
