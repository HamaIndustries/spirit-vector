package symbolics.division.spirit.vector.sfx;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import symbolics.division.spirit.vector.SpiritVectorSounds;

public class AudioGirl {
    public static void step(PlayerEntity player, BlockPos pos, BlockState state) {
//        if (player.getWorld().isClient) return;
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        SoundEvent step = SpiritVectorSounds.STEP;
//        System.out.println(Registries.SOUND_EVENT.getId(SpiritVectorSounds.FAKE));
        player.getWorld().playSound(
            null, pos.getX(), pos.getY(), pos.getZ(), SpiritVectorSounds.STEP, SoundCategory.PLAYERS, 1, 1
        );
    }
}
