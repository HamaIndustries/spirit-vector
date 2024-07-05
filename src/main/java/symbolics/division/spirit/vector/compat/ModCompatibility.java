package symbolics.division.spirit.vector.compat;

import net.fabricmc.loader.api.FabricLoader;
import symbolics.division.spirit.vector.SpiritVectorMod;

import java.lang.reflect.InvocationTargetException;

public interface ModCompatibility {

    void initialize(String modid, boolean inDev);

    String COMPAT_PACKAGE_PREFIX = "symbolics.division.spirit.vector.compat.";

    private static void tryInit(String compatClass, String modRef) {
        var loader = FabricLoader.getInstance();
        if (!loader.isModLoaded(modRef)) {
            SpiritVectorMod.LOGGER.debug("Did not find mod " + modRef + ", skipping compat check");
            return;
        }

        SpiritVectorMod.LOGGER.debug("Loading compat for mod " + modRef);

        String className = COMPAT_PACKAGE_PREFIX + compatClass;
        ModCompatibility compat;
        try {
             compat = (ModCompatibility)Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException err) {
            SpiritVectorMod.LOGGER.error("Unable to find compatibility class: " + className);
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                throw new RuntimeException(err);
            }
            return;
        } catch (NoSuchMethodException | InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException | ClassCastException err) {
            // you can tell from the number of exceptions we handle here that our code
            // is robust and sensible
            SpiritVectorMod.LOGGER.error("Failed to run constructor for: " + className);
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                throw new RuntimeException(err);
            }
            return;
        }
        compat.initialize(modRef, FabricLoader.getInstance().isDevelopmentEnvironment());
    }

    static void warnCompatBroken(String modid) {
        SpiritVectorMod.LOGGER.warn(SpiritVectorMod.MODID + " + " + modid + " integration failed. If you " +
                "are using the latest version of both, please notify the dev of " + SpiritVectorMod.MODID + ".");
    }

    static void alertRuntimeCompatBroken(String modid) {
        SpiritVectorMod.LOGGER.error("Mod " + modid + " probably had an API change probably not caught " +
                "in the compatability loading step. If you are using the latest version of both, " +
                "please report to the author of " + SpiritVectorMod.MODID + ".");
    }

    static void init() {
        SpiritVectorMod.LOGGER.debug("Loading " + SpiritVectorMod.MODID + " mod compatibilities");
        tryInit("DancerizerCompat", "dancerizer");
    }
}
