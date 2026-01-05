package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.modintegration.ModIntegration;

import java.util.Objects;
import java.util.Optional;

public class SRIGTOptionsTracker {
    private JsonObject lastOptions = null;

    public Optional<JsonObject> tick() {
        if (!ModIntegration.HAS_SPEEDRUNIGT) return Optional.empty();

        JsonObject newOptions = getSpeedRunIGTOptions();
        if (Objects.equals(lastOptions, newOptions)) return Optional.empty();

        JsonObject diff = lastOptions == null ? newOptions : HermesMod.getJsonDifference(lastOptions, newOptions);
        lastOptions = newOptions;

        return Optional.of(diff);
    }

    private static JsonObject getSpeedRunIGTOptions() {
        JsonObject out = new JsonObject();
        out.addProperty("current_run_category", ModIntegration.speedRunIGT$getRunCategory());
        // speedrunigt options, filtered because mcsrranked options are also included
        ModIntegration.speedRunIGT$getOptions().entrySet().stream().filter(e -> e.getKey().startsWith("speedrunigt:")).forEach(e -> out.addProperty(e.getKey(), e.getValue()));
        return out;
    }
}
