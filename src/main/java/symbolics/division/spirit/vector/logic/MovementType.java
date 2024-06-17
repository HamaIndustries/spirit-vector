package symbolics.division.spirit.vector.logic;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.move.BaseMovement;
import symbolics.division.spirit.vector.logic.move.SlideMovement;
import symbolics.division.spirit.vector.logic.move.WallJumpMovement;

import static symbolics.division.spirit.vector.SpiritVectorMod.id;

public interface MovementType {
    Identifier getID();

    // test if this movement applies to current context
    boolean testMovementCondition(SpiritVector sv, MovementContext ctx);

    // determine if a new movement should be selected
    boolean testMovementCompleted(SpiritVector sv, MovementContext ctx);

    // injects to vanilla LivingEntity.travel, possibly overriding.
    void travel(SpiritVector sv, MovementContext ctx);

    // disable slowdown from crouch and crawl
    default boolean preventSlowdown(SpiritVector sv) { return true; }

    // overrides entity movement speed
    default float getMovementSpeed(SpiritVector sv, float slip) {
        return sv.user.isOnGround() ? sv.user.getMovementSpeed() * (0.21600002F / (slip * slip * slip)) : 0.02F;
    };

    // update fuel and momentum
    void updateValues(SpiritVector sv);

    public static final MovementType NEUTRAL = new BaseMovement(id("neutral"));
    public static final MovementType SLIDE = new SlideMovement(id("slide"));
    public static final MovementType WALL_JUMP = new WallJumpMovement(id("wall_jump"));
//    public static MovementType VAULT = new MovementType(id("vault"));
//    public static MovementType GRIND = new MovementType(id("grind"));

}
