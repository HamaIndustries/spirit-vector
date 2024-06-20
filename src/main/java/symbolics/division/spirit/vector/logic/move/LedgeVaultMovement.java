package symbolics.division.spirit.vector.logic.move;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.JumpMovementContext;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.state.ManagedState;
import symbolics.division.spirit.vector.mixin.LivingEntityAccessor;

public class LedgeVaultMovement extends AbstractMovementType {
    private static final int MOMENTUM_GAINED = SpiritVector.MAX_MOMENTUM / 30;
    private static final int VAULT_WINDOW_TICKS = 5;
    private static final float VAULT_SPEED = 0.7f;
    private static final Identifier VAULT_STATE_ID = SpiritVectorMod.id("vault_window");

    public LedgeVaultMovement(Identifier id) {
        super(id);
    }

    public static void triggerLedge(SpiritVector sv) {
        sv.getStateManager().enableStateFor(VAULT_STATE_ID, VAULT_WINDOW_TICKS);
    }

    @Override
    public void register(SpiritVector sv) {
        sv.getStateManager().register(VAULT_STATE_ID, new ManagedState(sv));
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return sv.getStateManager().isActive(VAULT_STATE_ID) && ((LivingEntityAccessor)sv.user).isJumping() && sv.user.isOnGround();
    }

    @Override
    public boolean testMovementCompleted(SpiritVector sv, TravelMovementContext ctx) {
        return true;
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        sv.getStateManager().clearTicks(VAULT_STATE_ID);
        var dir = ctx.inputDir().normalize();
        if (dir.y < 0.3) { // ensure minimum upwards angle
            dir = dir.withAxis(Direction.Axis.Y, 0.3);
        }
        sv.user.addVelocity(dir.multiply(VAULT_SPEED));
        sv.getEffectsManager().spawnRing(sv.user.getWorld(), sv.user.getPos(), dir);
    }

    @Override
    public void jump(SpiritVector sv, JumpMovementContext ctx) {}

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(MOMENTUM_GAINED);
    }
}
