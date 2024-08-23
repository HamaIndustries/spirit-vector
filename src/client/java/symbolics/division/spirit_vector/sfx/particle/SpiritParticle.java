package symbolics.division.spirit_vector.sfx.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpiritParticle extends SpriteBillboardParticle {

    protected final SpriteProvider spriteProvider;

    protected SpiritParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.velocityMultiplier = 0.96F;
        this.spriteProvider = spriteProvider;
        this.velocityX *= 0.1F;
        this.velocityY *= 0.1F;
        this.velocityZ *= 0.1F;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        float g = 1.0F - (float)(Math.random() * 0.3F);
        this.red = g;
        this.green = g;
        this.blue = g;
        this.scale *= 1.4F;
        int i = (int)(8.0 / (Math.random() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
        this.collidesWithWorld = false;
        this.setSprite(this.spriteProvider.getSprite(world.getRandom()));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getSize(float tickDelta) {
        return MathHelper.clamp(
                scale * (-0.00065f*(this.age*this.age)+1),
                0,
                scale
        );
    }

    private static float NUDGE_THRESHOLD = 20f;

    @Override
    public void tick() {
        super.tick();
        if (!this.dead) {
            this.setSpriteForAge(this.spriteProvider);
            PlayerEntity playerEntity = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
            if (playerEntity != null && playerEntity.squaredDistanceTo(this.x, this.y, this.z) < NUDGE_THRESHOLD) {
                var v2 = new Vec3d(this.x - playerEntity.getX(), this.y - playerEntity.getY(), this.z - playerEntity.getZ())
                        .normalize().multiply(0.1f);
                this.setVelocity(v2.x, v2.y, v2.z);
            }
        }
    }

    @Override
    public void setSpriteForAge(SpriteProvider spriteProvider) {}

    public static class SpiritParticleFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public SpiritParticleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
            return new SpiritParticle(clientWorld, x, y, z, vx, vy, vz, this.spriteProvider);
        }
    }

}
