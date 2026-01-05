package me.duncanruns.hermes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.alive.Alive;
import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.core.InstanceInfo;
import me.duncanruns.hermes.instancestate.InstanceState;
import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.worldlog.WorldLog;
import me.duncanruns.hermes.worldpath.WorldPathHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class HermesMod implements ModInitializer {
    public static final String MOD_ID = "hermes";
    public static String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new IllegalStateException("Failed to find hermes version via fabric loader")).getMetadata().getVersion().getFriendlyString();
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final List<Runnable> CLOSE_RUNNABLES = new ArrayList<>();

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>... streams) {
        return Stream.of(streams).flatMap(s -> s);
    }

    @Environment(EnvType.CLIENT)
    public static @NotNull JsonObject screenToJsonObject(Screen currentScreen) {
        JsonObject data = new JsonObject();
        String screenClass = Optional.ofNullable(currentScreen).map(s -> s.getClass().getName()).orElse(null);
        JsonElement screenTitle = Optional.ofNullable(currentScreen).map(Screen::getTitle).map(Text.Serializer::toJsonTree).orElse(null);
        boolean screenIsPause = Optional.ofNullable(currentScreen).map(Screen::isPauseScreen).orElse(false);
        data.addProperty("class", screenClass);
        data.add("title", screenTitle);
        data.addProperty("is_pause", screenIsPause);
        return data;
    }

    public static Path getSavePath(MinecraftServer server) {
        return ((WorldPathHolder) server).hermes$getWorldPath();
    }

    /**
     * Converts a path to a json object. If the path is within the game directory, it will be marked as relative and
     * relativized to the game directory. Otherwise, it will be an absolute path with relative set to false.
     */
    public static JsonObject pathToJsonObject(Path path) {
        if (path == null) return null;
        JsonObject out = new JsonObject();
        if (path.toAbsolutePath().startsWith(HermesCore.GAME_DIR)) {
            out.addProperty("relative", true);
            out.addProperty("path", HermesCore.GAME_DIR.relativize(path).toString().replace("\\", "/"));
        } else {
            out.addProperty("relative", false);
            out.addProperty("path", path.toString().replace("\\", "/"));
        }
        return out;
    }

    public static void close() {
        CLOSE_RUNNABLES.forEach(runnable -> {
            try {
                runnable.run();
            } catch (Exception e) {
                LOGGER.error("Failed to run a close runnable: {}", e.getMessage());
            }
        });
    }

    public static void registerClose(Runnable runnable) {
        CLOSE_RUNNABLES.add(runnable);
    }

    public static JsonObject getJsonDifference(JsonObject previous, JsonObject current) {
        JsonObject difference = new JsonObject();
        current.entrySet().forEach(e -> {
            if (!Objects.equals(previous.get(e.getKey()), e.getValue())) {
                difference.add(e.getKey(), e.getValue());
            }
        });
        return difference;
    }

    @Override
    public void onInitialize() {
        if (!Files.exists(HermesCore.LOCAL_HERMES_FOLDER)) {
            try {
                Files.createDirectories(HermesCore.LOCAL_HERMES_FOLDER);
            } catch (Exception e) {
                LOGGER.error("Failed to create Hermes folder: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        Alive.init();
        PlayLog.init();
        InstanceState.init();
        if (HermesCore.IS_CLIENT) {
            InstanceInfo.setWorldLogPath(WorldLog.init());
        }
        HermesDisabledFeatures.getDisabledFeatures().forEach(InstanceInfo::addDisabledFeature);
        InstanceInfo.init();
    }
}