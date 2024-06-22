package symbolics.division.spirit.vector.sfx;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import symbolics.division.spirit.vector.SpiritVectorSounds;

import java.util.HashSet;
import java.util.Set;

public class AudioGirl {
    private static final Set<SoundEvent> trackedSounds = new HashSet<>();

    public static void step(PlayerEntity player, BlockState state) {
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        player.playSound(SpiritVectorSounds.STEP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
    }

    public static void burst(PlayerEntity player, BlockPos pos) {
//        player.playSound(
//                SpiritVectorSounds.BURST, 0.5f, 1
//        );
        player.getWorld().playSound(
                player, pos, SpiritVectorSounds.BURST, SoundCategory.PLAYERS, 0.5f, 1
        );
    }


}
