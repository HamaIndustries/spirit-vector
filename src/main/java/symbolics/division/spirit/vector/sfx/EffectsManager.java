package symbolics.division.spirit.vector.sfx;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import symbolics.division.spirit.vector.logic.SpiritVector;

import java.util.function.Consumer;

/*
    Nightmare static sludge DO NOT READ
    This would be a lot better but there's a week left
 */
public class EffectsManager {
    
    public static void acceptC2SPayload(SFXRequestPayload payload, ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        if (payload.type().equals(SFXRequestPayload.PARTICLE_EFFECT_TYPE)) {
            spawnParticleImpl((ServerWorld)player.getWorld(), payload.pack(), new Vec3d(payload.pos()));
        } else if (payload.type().equals(SFXRequestPayload.RING_EFFECT_TYPE)) {
            spawnRingImpl((ServerWorld)player.getWorld(), payload.pack(), new Vec3d(payload.pos()), new Vec3d(payload.dir()));
        }
    }

    private static Consumer<SFXRequestPayload> requestCallback = c -> {};
    public static void registerSFXRequestC2SCallback(Consumer<SFXRequestPayload> cb) {
        requestCallback = cb;
    }

    private final SpiritVector sv;

    public EffectsManager(SpiritVector sv) {
        this.sv = sv;
    }

    public void spawnParticle(World world, Vec3d pos) {
        if (world.isClient) {
            requestCallback.accept(new SFXRequestPayload(SFXRequestPayload.PARTICLE_EFFECT_TYPE, sv.getSFX(), pos.toVector3f(), new Vector3f()));
        } else {
            spawnParticleImpl((ServerWorld) world, sv.getSFX(), pos);
        }
    }

    public void spawnRing(World world, Vec3d pos, Vec3d dir) {
        if (world.isClient) {
            requestCallback.accept(new SFXRequestPayload(SFXRequestPayload.RING_EFFECT_TYPE, sv.getSFX(), pos.toVector3f(), dir.toVector3f()));
        } else {
            spawnRingImpl((ServerWorld) world, sv.getSFX(), pos, dir);
        }
    }

    // TODO a nonstaticified version of this
    private static void spawnParticleImpl(ServerWorld world, SFXPack<?> sfx, Vec3d pos) {
        world.spawnParticles(
                ParticleTypes.CHERRY_LEAVES, pos.x, pos.y, pos.z, 1, 0, 0, 0, 1
        );
    }

    private static void spawnRingImpl(ServerWorld world, SFXPack<?> sfx, Vec3d pos, Vec3d dir) {
        Vec3d[] uv = basis(dir.normalize());
        for (float i = 0; i <= Math.PI*2; i += Math.PI/12) {
            Vec3d p = pos.add(uv[0].multiply(Math.cos(i))).add(uv[1].multiply(Math.sin(i)));
            Vec3d d = p.subtract(pos);
            world.spawnParticles(
                    ParticleTypes.END_ROD, p.x, p.y, p.z, 1, d.x, d.y, d.z, 1
            );
        }
    }

    // return u, v for Householder reflector
    private static Vec3d[] basis(Vec3d vec) {
        double l = vec.length();
        double sigma = Math.signum(l);
        double h = vec.x + sigma;
        double beta = -1d / (sigma * h);

        Vec3d[] out = new Vec3d[2];
        double f = beta * vec.y;
        out[0] = new Vec3d(f*h, 1d+f*vec.y, f*vec.z);
        double g = beta * vec.z;
        out[1] = new Vec3d(g*h, g*vec.y, 1d+g*vec.z);
        return out;
    }

}
