package me.duncanruns.hermes.modintegration;

import net.fabricmc.loader.api.FabricLoader;

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
}
