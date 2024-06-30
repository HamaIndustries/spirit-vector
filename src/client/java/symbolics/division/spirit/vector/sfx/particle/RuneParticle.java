package symbolics.division.spirit.vector.sfx.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class RuneParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    protected RuneParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.velocityMultiplier = 0.96F;
        this.spriteProvider = spriteProvider;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = 0;
        this.velocityY = Math.max(this.velocityY, 0.01);
        this.velocityZ = 0;
        this.scale *= 1.4F;
        int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
        this.collidesWithWorld = false;
        this.setSprite(this.spriteProvider.getSprite(world.getRandom()));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale;
    }

    @Override
    public void tick() {
        if (!this.dead) {
            this.velocityY *= 1.1;
        }
        super.tick();
    }

    @Override
    public void setSpriteForAge(SpriteProvider spriteProvider) {}

    public static class RuneParticleFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public RuneParticleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
            return new RuneParticle(clientWorld, x, y, z, vx, vy, vz, this.spriteProvider);
        }
    }
}
