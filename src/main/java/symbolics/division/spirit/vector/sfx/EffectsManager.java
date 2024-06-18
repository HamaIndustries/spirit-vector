package symbolics.division.spirit.vector.sfx;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import symbolics.division.spirit.vector.logic.ISpiritVectorUser;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.sfx.SFXPack;

import java.util.function.Consumer;

// acts as interface to controller
// manages state of particles and animations
/*
    Acts as opaque interface to effects system
    spirit vector issues commands and this enacts them
    spirit vector calcs are usually done on client, but
    all particle effects should be done by server. this
    class normalizes all commands to run on server.
 */
public class EffectsManager {
    public static void acceptC2SPayload(SFXRequestPayload payload, ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        if (payload.type().equals(SFXRequestPayload.PARTICLE_EFFECT_TYPE)) {
            spawnParticleImpl(player.getWorld(), payload.pack(), new Vec3d(payload.pos()));
        } else if (payload.type().equals(SFXRequestPayload.RING_EFFECT_TYPE)) {
            spawnRingImpl(player.getWorld(), payload.pack(), new Vec3d(payload.pos()), new Vec3d(payload.dir()));
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
            spawnParticleImpl(world, sv.getSFX(), pos);
        }

    }

    public void spawnRing(World world, Vec3d pos, Vec3d dir) {
        if (world.isClient) {
            requestCallback.accept(new SFXRequestPayload(SFXRequestPayload.RING_EFFECT_TYPE, sv.getSFX(), pos.toVector3f(), dir.toVector3f()));
        } else {
            spawnRingImpl(world, sv.getSFX(), pos, dir);
        }
    }


    // TODO a nonstaticified version of this
    private static void spawnParticleImpl(World world, SFXPack<?> sfx, Vec3d pos) {
        System.out.println("particle spawn " + pos);
    }

    private static void spawnRingImpl(World world, SFXPack<?> sfx, Vec3d pos, Vec3d dir) {
        System.out.println("ring spawn " + pos + " " + dir);
    }

}
