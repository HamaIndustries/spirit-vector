package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.skates.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;

import static symbolics.division.spirit.vector.SpiritVectorMod.id;

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

    MovementType NEUTRAL = new NeutralMovement(id("neutral"));
    MovementType SLIDE = new SlideMovement(id("slide"));
    MovementType WALL_JUMP = new WallJumpMovement(id("wall_jump"));
    MovementType VAULT = new LedgeVaultMovement(id("vault"));
    MovementType JUMP = new JumpingMovement(id("jump"));
    MovementType WALL_RUSH = new WallRushMovement(id("wall_rush"));
    MovementType STOP = new HardstopMovement(id("hard_stop"));
//    public static MovementType GRIND = new MovementType(id("grind"));

}
