package symbolics.division.spirit_vector.logic;

import org.jetbrains.annotations.Nullable;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;

import java.util.Optional;

public interface ISpiritVectorUser {
    @Nullable SpiritVector spiritVector();
    default Optional<SpiritVector> getSpiritVector() {
        return Optional.ofNullable(spiritVector());
    };
    void setWingState(boolean visible);
}
