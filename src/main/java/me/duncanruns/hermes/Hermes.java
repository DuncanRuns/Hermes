package me.duncanruns.hermes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.alive.Alive;
import me.duncanruns.hermes.instanceinfo.InstanceInfo;
import me.duncanruns.hermes.instancestate.InstanceState;
import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.worldlog.WorldLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Hermes implements ModInitializer {
    public static final String MOD_ID = "hermes";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Path GAME_DIR = FabricLoader.getInstance().getGameDir().normalize().toAbsolutePath();
    public static final Path LOCAL_HERMES_FOLDER = GAME_DIR.resolve("hermes");
    public static final boolean IS_CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
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
        return server.getSavePath(WorldSavePath.ROOT);
    }

    public static long getProcessId() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int atIndex = jvmName.indexOf('@');
        if (atIndex > 0) {
            try {
                return Long.parseLong(jvmName.substring(0, atIndex));
            } catch (NumberFormatException e) {
                // Unexpected format, fallback
            }
        }
        throw new IllegalStateException("Unable to determine process ID from JVM name: " + jvmName);
    }

    /**
     * Converts a path to a json object. If the path is within the game directory, it will be marked as relative and
     * relativized to the game directory. Otherwise, it will be an absolute path with relative set to false.
     */
    public static JsonObject pathToJsonObject(Path path) {
        if (path == null) return null;
        JsonObject out = new JsonObject();
        if (path.toAbsolutePath().startsWith(GAME_DIR)) {
            out.addProperty("relative", true);
            out.addProperty("path", GAME_DIR.relativize(path).toString().replace("\\", "/"));
        } else {
            out.addProperty("relative", false);
            out.addProperty("path", path.toString().replace("\\", "/"));
        }
        return out;
    }

    @Override
    public void onInitialize() {
        if (!Files.exists(LOCAL_HERMES_FOLDER)) {
            try {
                Files.createDirectories(LOCAL_HERMES_FOLDER);
            } catch (Exception e) {
                LOGGER.error("Failed to create Hermes folder: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        InstanceInfo.init();
        Alive.init();
        PlayLog.init();
        InstanceState.init();
        if (IS_CLIENT) {
            WorldLog.init();
        }
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
}