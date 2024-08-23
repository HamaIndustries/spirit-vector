package symbolics.division.spirit_vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;
import symbolics.division.spirit_vector.logic.TravelMovementContext;
import symbolics.division.spirit_vector.SpiritVectorMod;

public interface MovementType {
    Identifier getID();

    // set up sv with states and values needed for this movement
    default void configure(SpiritVector sv) {}

    // test if this movement applies to current context
    boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx);

    // determine if a new movement should be selected
    boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx);

    // injects to vanilla LivingEntity.travel, possibly overriding.
    void travel(SpiritVector sv, TravelMovementContext ctx);

    // update fuel and momentum
    void updateValues(SpiritVector sv);

    default boolean disableDrag(SpiritVector sv) { return false; }

    default void exit(SpiritVector sv) {}

    default boolean fluidMovementAllowed(SpiritVector sv) { return false; }

    default float safeFallDistance(SpiritVector sv) {
        return 5;
    }

    default String getTranslationKey() {
        return getID().withPrefixedPath("abilities.").toTranslationKey();
    }

    MovementType NEUTRAL = new NeutralMovement(SpiritVectorMod.id("neutral"));
    MovementType SLIDE = new SlideMovement(SpiritVectorMod.id("slide"));
    MovementType WALL_JUMP = new WallJumpMovement(SpiritVectorMod.id("wall_jump"));
    MovementType VAULT = new LedgeVaultMovement(SpiritVectorMod.id("vault"));
    MovementType JUMP = new JumpingMovement(SpiritVectorMod.id("jump"));
    MovementType WALL_RUSH = new WallRushMovement(SpiritVectorMod.id("wall_rush"));
//    public static MovementType GRIND = new MovementType(id("grind"));

}
