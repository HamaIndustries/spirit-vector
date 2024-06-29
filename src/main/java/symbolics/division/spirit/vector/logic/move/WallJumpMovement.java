package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.input.Input;

public class WallJumpMovement extends AbstractMovementType {
    private static final int MOMENTUM_GAINED = SpiritVector.MAX_MOMENTUM / 20;

    public WallJumpMovement(Identifier id) {
        super(id);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return !sv.user.isOnGround()
                && MovementUtils.idealWalljumpingConditions(sv, ctx)
                && sv.inputManager().consume(Input.JUMP);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
//        var input = MovementUtils.augmentedInput(sv, ctx);
        var input = MovementUtils.getWalljumpingInput(sv, ctx);
        Vec3d motion = new Vec3d(input.x/2, 0.5, input.z/2);
        if (sv.getMoveState() == this) { // only apply for normal walljumps
            motion = motion.multiply(sv.consumeSpeedMultiplier());
        }
        sv.user.setVelocity(motion);
        sv.effectsManager().spawnRing(sv.user.getPos(), motion);
    }

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(MOMENTUM_GAINED);
    }
}
