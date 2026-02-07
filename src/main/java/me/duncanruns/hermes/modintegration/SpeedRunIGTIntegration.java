package me.duncanruns.hermes.modintegration;

import com.redlimerl.speedrunigt.option.SpeedRunOption;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

public final class SpeedRunIGTIntegration {
    private static Field optionsField = null;

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

    public static String getRunCategory() {
        return InGameTimer.getInstance().getCategory().getID();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getOptions() throws NoSuchFieldException, IllegalAccessException {
        if (optionsField == null) {
            optionsField = SpeedRunOption.class.getDeclaredField("options");
            optionsField.setAccessible(true);
        }

        Map<Identifier, String> options = (Map<Identifier, String>) optionsField.get(null);
        return options.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
    }
}
