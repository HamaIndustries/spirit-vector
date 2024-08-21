package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.skates.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.move.MovementType;
import symbolics.division.spirit.vector.logic.state.ManagedState;

public class WaterRunAbility extends AbstractSpiritVectorAbility {
    protected static final Identifier WATER_RUN_FLAG = SpiritVectorMod.id("water_run_flag");

    public WaterRunAbility(Identifier id) {
        super(id, Integer.MAX_VALUE);
    }

    public static boolean canWaterRun(SpiritVector sv) {
        // todo: expensive, should be cached per tick
        return sv.stateManager().getOptional(WATER_RUN_FLAG).isPresent()
                && sv.user.getVelocity().lengthSquared() > 0.3
                && sv.user.isTouchingWater()
                && !sv.user.isSubmergedInWater()
                && MathHelper.fractionalPart(sv.user.getPos().y) >= 0.5
                && sv.getMomentum() > 0
                && sv.user.getWorld().getBlockState(sv.user.getBlockPos().up()).getFluidState().isEmpty();
    }

    public static boolean isWaterRunning(SpiritVector sv) {
        return canWaterRun(sv) && sv.getMoveState() == MovementType.SLIDE;
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        return false;
    }

    @Override
    public void configure(SpiritVector sv) {
        sv.stateManager().register(WATER_RUN_FLAG, new ManagedState(sv));
    }

    @Override public void travel(SpiritVector sv, TravelMovementContext ctx) {}
}
