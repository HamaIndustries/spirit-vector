package symbolics.division.spirit.vector.logic;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.move.BaseMovement;
import symbolics.division.spirit.vector.logic.move.SlideMovement;
import symbolics.division.spirit.vector.logic.move.WallJumpMovement;

import static symbolics.division.spirit.vector.SpiritVectorMod.id;

public interface MovementType {
    Identifier getID();

    // test if this movement applies to current context
    boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx);

    // determine if a new movement should be selected
    boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx);

    // injects to vanilla LivingEntity.travel, possibly overriding.
    default void travel(SpiritVector sv, TravelMovementContext ctx) {};

    // injects to vanilla LivingEntity.jump, possibly overriding.
    default void jump(SpiritVector sv, JumpMovementContext ctx)  {};

    // disable slowdown from crouch and crawl
    default boolean preventSlowdown(SpiritVector sv) { return true; }

    // overrides entity movement speed w/ slip
    default float getMovementSpeed(SpiritVector sv, float slip) {
        return sv.user.isOnGround() ? sv.user.getMovementSpeed() * (0.21600002F / (slip * slip * slip)) : 0.02F;
    };

    // update fuel and momentum
    void updateValues(SpiritVector sv);

    default boolean disableDrag(SpiritVector sv) { return false; }

    default void exit(SpiritVector sv) {}

    public static final MovementType NEUTRAL = new BaseMovement(id("neutral"));
    public static final MovementType SLIDE = new SlideMovement(id("slide"));
    public static final MovementType WALL_JUMP = new WallJumpMovement(id("wall_jump"));
//    public static MovementType VAULT = new MovementType(id("vault"));
//    public static MovementType GRIND = new MovementType(id("grind"));

}
