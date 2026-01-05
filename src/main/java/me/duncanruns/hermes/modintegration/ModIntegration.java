package me.duncanruns.hermes.modintegration;

import me.duncanruns.hermes.HermesMod;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class ModIntegration {
    public static final boolean HAS_SPEEDRUNIGT = FabricLoader.getInstance().isModLoaded("speedrunigt");
    public static final boolean HAS_ATUM = FabricLoader.getInstance().isModLoaded("atum");

    private ModIntegration() {
    }

    public static long speedRunIGT$getRTA() {
        if (HAS_SPEEDRUNIGT) {
            return SpeedRunIGTIntegration.getRTA();
        }
        return -1;
    }

    public static long speedRunIGT$getIGT() {
        if (HAS_SPEEDRUNIGT) {
            return SpeedRunIGTIntegration.getIGT();
        }
        return -1;
    }

    public static long speedRunIGT$getRetime() {
        if (HAS_SPEEDRUNIGT) {
            return SpeedRunIGTIntegration.getRetime();
        }
        return -1;
    }

    public static boolean atum$isRunning() {
        if (HAS_ATUM) {
            return AtumIntegration.isRunning();
        }
        return false;
    }

    public static String speedRunIGT$getRunCategory() {
        if (HAS_SPEEDRUNIGT) {
            return SpeedRunIGTIntegration.getRunCategory();
        }
        return "";
    }

    public static Map<String, String> speedRunIGT$getOptions() {
        if (HAS_SPEEDRUNIGT) {
            try {
                return SpeedRunIGTIntegration.getOptions();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                HermesMod.LOGGER.error("Failed to get SpeedRunIGT options: {}", e.getMessage());
            }
        }
        return Collections.emptyMap();
    }
}
