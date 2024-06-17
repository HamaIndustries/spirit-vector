package symbolics.division.spirit.vector.sfx;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import symbolics.division.spirit.vector.SpiritVectorMod;

import java.util.ArrayList;

public interface SFXPack<T extends ParticleEffect> {
    public abstract Identifier id();
    public abstract Identifier wingsTexture();
    public abstract ParticleType<T> particleType();

    /*
    - butterfly wings (gold (beatoriceeeeeee))
    - bird wings (blue)
    - V1's wings (yellow)
    - lemma's wings (green)
    - dragon wings (red)
    - fairy wings? bug wings? (pink)
    - evangelion light wings (white)
     */

    public static final SimpleSFX BUTTERFLY = new SimpleSFX(SpiritVectorMod.id("butterfly"));
    // public static final SFXPack BIRD
    // public static final SFXPack V1
    // public static final SFXPack ROBO
    // public static final SFXPack DRAGON
    // public static final SFXPack FAIRY
    // public static final SFXPack ANGEL
}
