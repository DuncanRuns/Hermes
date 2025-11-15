package me.duncanruns.hermes.modintegration;

import com.redlimerl.speedrunigt.timer.InGameTimer;

public final class SpeedRunIGTIntegration {
    private SpeedRunIGTIntegration() {
    }

    public static long getRTA() {
        return InGameTimer.getInstance().getRealTimeAttack();
    }

    public static long getIGT() {
        return InGameTimer.getInstance().getInGameTime();
    }

    public static long getRetime() {
        return InGameTimer.getInstance().getRetimedInGameTime();
    }
}
