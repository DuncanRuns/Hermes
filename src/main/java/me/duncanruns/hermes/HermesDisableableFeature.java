package me.duncanruns.hermes;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public enum HermesDisableableFeature {
    PLAY_LOG("playlog", "Play Log"),
    GHOST("ghost", "Ghost"),
    ATUM_INTEGRATION("atum_integration_v1", "Atum Integration"),
    SPEEDRUNIGT_INTEGRATION("speedrunigt_integration_v1", "SpeedRunIGT Integration");

    private static final Map<String, HermesDisableableFeature> IDENTIFIER_MAP = new HashMap<>();
    private final String identifier;
    private final String displayName;
    private boolean disabled = false;

    static {
        for (HermesDisableableFeature value : HermesDisableableFeature.values()) {
            IDENTIFIER_MAP.put(value.identifier, value);
        }
        FabricLoader.getInstance().getAllMods().forEach(HermesDisableableFeature::checkMod);
    }

    HermesDisableableFeature(String identifier, String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
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

    private static void disable(String identifier, String modId) {
        Optional.ofNullable(IDENTIFIER_MAP.get(identifier))
                .ifPresent(feat -> feat.disable(modId));
    }

    public boolean isDisabled() {
        return disabled;
    }

    private void disable(String sourceMod) {
        if (disabled) return;
        disabled = true;
        //noinspection StringConcatenationArgumentToLogCall
        HermesMod.LOGGER.info(displayName + " feature disabled by mod '" + sourceMod + "'");
    }

    public static List<String> getDisabledFeatures() {
        return Arrays.stream(HermesDisableableFeature.values())
                .filter(HermesDisableableFeature::isDisabled)
                .map(feat -> feat.identifier)
                .collect(Collectors.toList());
    }
}
