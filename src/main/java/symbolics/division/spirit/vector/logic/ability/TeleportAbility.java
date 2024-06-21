package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;

public class TeleportAbility extends AbstractSpiritVectorAbility {
    public static final float TELEPORT_DISTANCE = 7;

    public TeleportAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 2);
    }

    @Override
    public boolean testMovementCondition(SpiritVector sv, TravelMovementContext ctx) {
        var delta = sv.user.getRotationVecClient().multiply(TELEPORT_DISTANCE);
        return sv.user.doesNotCollide(delta.x, delta.y, delta.z);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        var delta = sv.user.getRotationVecClient().multiply(TELEPORT_DISTANCE);
        sv.effectsManager().spawnRing(sv.user.getPos().add(delta), delta);
        var ds = delta.add(sv.user.getPos());
        TeleportAbilityC2SPayload.requestTeleport(ds);
        ctx.ci().cancel();
    }

    @Override
    public void updateValues(SpiritVector sv) {
        sv.modifyMomentum(-sv.getMomentum());
    }
}
