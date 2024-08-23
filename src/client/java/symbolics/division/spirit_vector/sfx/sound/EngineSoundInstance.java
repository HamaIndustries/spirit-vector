package symbolics.division.spirit_vector.sfx.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import symbolics.division.spirit_vector.SpiritVectorSounds;
import symbolics.division.spirit_vector.logic.ISpiritVectorUser;
import symbolics.division.spirit_vector.logic.vector.SpiritVector;

public class EngineSoundInstance extends MovingSoundInstance {

    public static boolean shouldPlayFor(PlayerEntity player) {
        return !player.isRemoved()
                && SpiritVector.hasEquipped(player)
                && !player.isInFluid();
    }

    private static final float VOLUME_RELATIVE = 0.25f;
    private final PlayerEntity player;

    public EngineSoundInstance(PlayerEntity player) {
        super(SpiritVectorSounds.ENGINE, SoundCategory.BLOCKS, player.getRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.8f;
        this.pitch = 0.8f;
    }

    @Override
    public void tick() {
        float speed = (float)this.player.getVelocity().length();
        if (shouldPlayFor(this.player)) {
            this.volume = Math.min(0.5f, speed) / 0.5f * VOLUME_RELATIVE;
            if (player instanceof ISpiritVectorUser user) {
                user.getSpiritVector().ifPresent(
                        sv -> this.pitch = ((float)sv.getMomentum() / (float)SpiritVector.MAX_MOMENTUM * 0.5f) + 0.5f + (player.getRandom().nextFloat() * 0.2f - 0.1f)
                );
            }
            this.x = this.player.getX();
            this.y = this.player.getY();
            this.z = this.player.getZ();
        } else {
            this.setDone();
        }
    }
}
