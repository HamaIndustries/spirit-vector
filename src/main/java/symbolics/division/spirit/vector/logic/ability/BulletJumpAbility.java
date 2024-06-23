package symbolics.division.spirit.vector.logic.ability;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.logic.TravelMovementContext;
import symbolics.division.spirit.vector.logic.move.MovementType;

public class BulletJumpAbility extends AbstractSpiritVectorAbility {
    public static final float SPEED_MULTIPLIER = 8;

    public BulletJumpAbility(Identifier id) {
        super(id, SpiritVector.MAX_MOMENTUM / 3);
    }

    @Override
    public void travel(SpiritVector sv, TravelMovementContext ctx) {
        Vec3d dir = sv.user.getRotationVecClient();
        sv.user.setVelocity(dir.multiply(sv.getMovementSpeed() * SPEED_MULTIPLIER));
        sv.effectsManager().spawnRing(sv.user.getPos(), dir);
        MovementType.NEUTRAL.travel(sv, ctx);
    }
}
