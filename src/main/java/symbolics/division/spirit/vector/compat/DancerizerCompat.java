package symbolics.division.spirit.vector.compat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import symbolics.division.spirit.vector.SpiritVectorMod;
import symbolics.division.spirit.vector.logic.SpiritVector;
import symbolics.division.spirit.vector.networking.ModifyMomentumPayloadS2C;

import java.lang.reflect.UndeclaredThrowableException;

public class DancerizerCompat implements ModCompatibility {
    private static final int TAUNT_MOMENTUM_GAIN = SpiritVector.MAX_MOMENTUM / 5;

    @Override
    public void initialize(String modid, boolean inDev) {
        SpiritVectorMod.LOGGER.debug("Dancerizer setup");
        theCoolerInitialize(modid, inDev);
    }

    @SuppressWarnings("unchecked")
    public <T> void theCoolerInitialize(String modid, boolean inDev) {
        SpiritVectorMod.LOGGER.debug("Dancerizer compat loading");
        // this is how they say
        // PlayerAnimationCallback.EVENT.register(DancerizerCompat::momentumGainCallback);
        // in the void tongue

        try {
            // yoink sammy's api from the void
            Class<T> cbInterface = (Class<T>)Class.forName("dev.kleinbox.dancerizer.common.api.PlayerAnimationCallback");
            // prepare its conjuration actuator
            var event = (Event<T>)cbInterface.getDeclaredField("EVENT").get(null);

            //(｀･ω･)ﾉ☆･ﾟ::ﾟ
            var cbProxy = java.lang.reflect.Proxy.newProxyInstance(
                    cbInterface.getClassLoader(),
                    new java.lang.Class[] {cbInterface},
                    (proxy, method, args) -> {
                        if (args.length != 2) {
                            throw new NoSuchFieldException("Expected 2 arguments, got " + args.length);
                        }
                        return momentumGainCallback(args[0], args[1]);
                    }
            );

            // cast it back into the blind eternities
            event.register(cbInterface.cast(cbProxy));
        } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException|ClassCastException err) {
            if (inDev) {
                throw new RuntimeException(err);
            } else {
                ModCompatibility.warnCompatBroken(modid);
            }
        } catch (UndeclaredThrowableException err) {
            if (err.getUndeclaredThrowable() instanceof NoSuchFieldException) {
                ModCompatibility.alertRuntimeCompatBroken(modid);
            }
            throw new RuntimeException(err.getUndeclaredThrowable());
        }
    }

    private static ActionResult momentumGainCallback(Object source, Object IGNORED_ANIM_TYPE) {
        if (source instanceof ServerPlayerEntity player && player.speed > 2 && !player.isOnGround()) {
            ServerPlayNetworking.send(player, new ModifyMomentumPayloadS2C(TAUNT_MOMENTUM_GAIN, true));
        }
        return ActionResult.PASS;
    }
}
