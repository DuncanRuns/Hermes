package me.duncanruns.hermes.modintegration;

import me.voidxwalker.autoreset.Atum;

public final class AtumIntegration {
    private AtumIntegration() {
    }

    public static boolean isRunning() {
        return Atum.isRunning();
    }
}
