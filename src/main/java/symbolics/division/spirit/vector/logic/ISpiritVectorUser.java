package symbolics.division.spirit.vector.logic;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ISpiritVectorUser {
    @Nullable SpiritVector spiritVector();
    default Optional<SpiritVector> getSpiritVector() {
        return Optional.ofNullable(spiritVector());
    };
    void setWingState(boolean visible);
}
