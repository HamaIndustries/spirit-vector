package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.input.Input;
import symbolics.division.spirit.vector.logic.state.ManagedState;

public class LedgeVaultMovement extends AbstractMovementType {
    private static final int MOMENTUM_GAINED = SpiritVector.MAX_MOMENTUM / 30;
    private static final int VAULT_WINDOW_TICKS = 5;
    private static final float VAULT_SPEED = 1.2f;
    private static final Identifier VAULT_STATE_ID = SpiritVectorMod.id("vault_window");

    public LedgeVaultMovement(Identifier id) {
        super(id);
    }

    public static void triggerLedge(SpiritVector sv) {
        sv.stateManager().enableStateFor(VAULT_STATE_ID, VAULT_WINDOW_TICKS);
    }

    @Override
    public void configure(SpiritVector sv) {
        sv.stateManager().register(VAULT_STATE_ID, new ManagedState(sv));
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return sv.stateManager().isActive(VAULT_STATE_ID) && sv.user.isOnGround() && sv.inputManager().consume(Input.JUMP);
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        return true;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        sv.stateManager().clearTicks(VAULT_STATE_ID);
        Vec3d result;
        if (ctx.inputDir().lengthSquared() < 0.1) {          // no input, vault
            result = new Vec3d(0, 0.9, 0);
        } else {                                             // input: ledgetrick
            // remove this check if its too confusing
            double y = Math.abs(sv.user.getRotationVector().y);
            result = ctx.inputDir().withAxis(Direction.Axis.Y, Math.max(0.3, y)).normalize();
        }
        sv.user.addVelocity(result.multiply(VAULT_SPEED *  sv.consumeSpeedMultiplier()));
        sv.effectsManager().spawnRing(sv.user.getPos(), result);
    }

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(MOMENTUM_GAINED);
    }
}
