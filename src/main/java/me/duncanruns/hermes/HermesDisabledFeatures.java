package me.duncanruns.hermes;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HermesDisabledFeatures {
    private static final Gson GSON = new com.google.gson.Gson();
    private static final String PLAY_LOG = "playlog";
    private static final String GHOST = "ghost";
    private static boolean playLogDisabled = false;
    private static boolean ghostDisabled = false;
    private static final List<String> disabledFeatures = new ArrayList<>();

    static {
        FabricLoader.getInstance().getAllMods().forEach(HermesDisabledFeatures::checkMod);
    }

    private static void checkMod(ModContainer modContainer) {
        String modId = modContainer.getMetadata().getId();
        Stream.of(modContainer)
                .map(c -> c.getMetadata().getCustomValue("hermes:disable"))
                .filter(Objects::nonNull)
                .filter(c -> c.getType().equals(CustomValue.CvType.ARRAY))
                .map(CustomValue::getAsArray)
                .flatMap(c -> StreamSupport.stream(c.spliterator(), false))
                .filter(c -> c.getType().equals(CustomValue.CvType.STRING))
                .map(CustomValue::getAsString)
                .forEach(s -> disable(s, modId));
    }

    private static void disable(String feature, String modId) {
        switch (feature) {
            case PLAY_LOG:
                if (playLogDisabled) return;
                playLogDisabled = true;
                disabledFeatures.add(PLAY_LOG);
                HermesMod.LOGGER.info("Play Log feature disabled by mod '{}'", modId);
                break;
            case GHOST:
                if (ghostDisabled) return;
                ghostDisabled = true;
                disabledFeatures.add(GHOST);
                HermesMod.LOGGER.info("Ghost feature disabled by mod '{}'", modId);
                break;
        }
    }

    public static boolean isPlayLogDisabled() {
        return playLogDisabled;
    }

    public static boolean isGhostDisabled() {
        return ghostDisabled;
    }

    public static List<String> getDisabledFeatures() {
        return Collections.unmodifiableList(disabledFeatures);
    }
}
