package symbolics.division.spirit.vector.sfx.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import symbolics.division.spirit.vector.SpiritVectorSounds;
import symbolics.division.spirit.vector.logic.move.MovementUtils;

public class SlidingSoundInstance extends MovingSoundInstance {

    public static boolean shouldPlayFor(PlayerEntity player) {
        if (EngineSoundInstance.shouldPlayFor(player) && player.isInPose(EntityPose.CROUCHING)) {
            return player.isOnGround()
                    || MovementUtils.idealWallrunningConditions(player.getWorld(), player.getPos());
        }
        return false;
    }

    private static final float VOLUME_RELATIVE = 0.2f;
    private static final float WINDUP_TICKS = 10;
    private final PlayerEntity player;
    private float ticksPlaying = 8;

    public SlidingSoundInstance(PlayerEntity player) {
        super(SpiritVectorSounds.SLIDE, SoundCategory.BLOCKS, player.getRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.8f;
        this.pitch = 0.25f;
    }

    @Override
    public void tick() {
        ticksPlaying = Math.min(ticksPlaying+1, WINDUP_TICKS);
        float speedSq = (float)this.player.getVelocity().lengthSquared();
        if (!this.player.isRemoved() && shouldPlayFor(this.player)) {
            this.volume = Math.min(0.5f, speedSq) / 0.5f * (ticksPlaying / WINDUP_TICKS) * VOLUME_RELATIVE;
            this.pitch = 0.2f + (player.getRandom().nextFloat() * 0.1f);
            this.x = this.player.getX();
            this.y = this.player.getY();
            this.z = this.player.getZ();
        } else {
            this.setDone();
        }
    }
}
