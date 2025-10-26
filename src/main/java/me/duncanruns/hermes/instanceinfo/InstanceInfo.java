package me.duncanruns.hermes.instanceinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.Hermes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public final class InstanceInfo {
    public static final Path GLOBAL_HERMES_PATH = getGlobalHermesPath();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static RandomAccessFile file;
    private static FileLock fileLock;

    private InstanceInfo() {
    }

    private static Path getGlobalHermesPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String folderName = "HermesMCInstances";

        if (os.contains("win")) {
            // Windows → %LOCALAPPDATA%\Temp\{folderName}
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null && !localAppData.isEmpty()) {
                return Paths.get(localAppData, "Temp", folderName);
            }
            // Fallback to Java temp dir
            return Paths.get(System.getProperty("java.io.tmpdir"), folderName);
        } else if (os.contains("mac")) {
            // macOS → java.io.tmpdir/{folderName} (usually /var/folders/.../T)
            return Paths.get(System.getProperty("java.io.tmpdir"), folderName);
        } else {
            // Linux/Unix
            String runtimeDir = System.getenv("XDG_RUNTIME_DIR");
            if (runtimeDir != null && !runtimeDir.isEmpty()) {
                return Paths.get(runtimeDir, folderName);
            }
            // Fallback to /tmp if XDG_RUNTIME_DIR not set
            return Paths.get("/tmp", folderName);
        }
    }

    public static void init() {
        Hermes.LOGGER.info("Global Hermes Folder: {}", GLOBAL_HERMES_PATH);
        if (!Files.exists(GLOBAL_HERMES_PATH)) {
            try {
                Hermes.LOGGER.info("Creating Global Hermes Folder...");
                Files.createDirectories(GLOBAL_HERMES_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        long pid;
        try {
            pid = Hermes.getProcessId();
            Hermes.LOGGER.info("Current PID: {}", pid);
        } catch (Exception e) {
            pid = -1;
            Hermes.LOGGER.error("Failed to get PID: {}", e.getMessage());
        }

        JsonObject instanceJson = new JsonObject();
        if (pid != -1) instanceJson.addProperty("pid", pid);
        instanceJson.addProperty("is_server", !Hermes.IS_CLIENT);
        instanceJson.addProperty("game_dir", FabricLoader.getInstance().getGameDir().toAbsolutePath().toString());
        instanceJson.addProperty("game_version", SharedConstants.getGameVersion().getName());
        JsonArray modsArray = new JsonArray();

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            JsonObject modObject = new JsonObject();
            modObject.addProperty("name", modContainer.getMetadata().getName());
            modObject.addProperty("id", modContainer.getMetadata().getId());
            modObject.addProperty("version", modContainer.getMetadata().getVersion().getFriendlyString());
            modsArray.add(modObject);
        });
        instanceJson.add("mods", modsArray);

        String fileName;
        if (pid == -1) {
            fileName = "unknown-" + System.currentTimeMillis() + "-" + new Random().nextLong();
        } else {
            fileName = String.valueOf(pid);
        }
        Path pidFile = GLOBAL_HERMES_PATH.resolve(fileName + ".json");

        try {
            file = new RandomAccessFile(pidFile.toFile(), "rw");
            file.setLength(0);
            file.seek(0);
            file.write(GSON.toJson(instanceJson).getBytes());
            fileLock = file.getChannel().tryLock(0L, Long.MAX_VALUE, true);
            Hermes.registerClose(() -> close(pidFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void close(Path pidFile) {
        try {
            fileLock.release();
            file.close();
            Files.delete(pidFile);
        } catch (Throwable ignored) {
        }
    }
}
