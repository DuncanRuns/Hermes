package me.duncanruns.hermes.instanceinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public final class InstanceInfo {
    public static final Path DIRECTORY = HermesMod.GLOBAL_HERMES_FOLDER.resolve("Instances");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static RandomAccessFile file;
    private static FileLock fileLock;

    private InstanceInfo() {
    }


    public static void init() {
        ensureFolder();
        createInstanceInfoFile();
    }

    private static void createInstanceInfoFile() {
        long pid = getPid();
        JsonObject instanceJson = getInstanceInfoJson(pid);
        Path instanceInfoFilePath = getFilePath(pid);
        writeAndLock(instanceInfoFilePath, instanceJson);
    }

    private static void writeAndLock(Path instanceInfoFilePath, JsonObject instanceJson) {
        try {
            file = new RandomAccessFile(instanceInfoFilePath.toFile(), "rw");
            file.setLength(0);
            file.seek(0);
            file.write(GSON.toJson(instanceJson).getBytes());
            fileLock = file.getChannel().tryLock(0L, Long.MAX_VALUE, true);
            HermesMod.registerClose(() -> close(instanceInfoFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull JsonObject getInstanceInfoJson(long pid) {
        JsonObject instanceJson = new JsonObject();
        if (pid != -1) instanceJson.addProperty("pid", pid);
        instanceJson.addProperty("is_server", !HermesMod.IS_CLIENT);
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
        return instanceJson;
    }

    private static @NotNull Path getFilePath(long pid) {
        String fileName;
        if (pid == -1) {
            fileName = "unknown-" + System.currentTimeMillis() + "-" + new Random().nextLong();
        } else {
            fileName = String.valueOf(pid);
        }
        return DIRECTORY.resolve(fileName + ".json");
    }

    private static long getPid() {
        long pid;
        try {
            pid = HermesMod.getProcessId();
            HermesMod.LOGGER.info("Current PID: {}", pid);
        } catch (Exception e) {
            pid = -1;
            HermesMod.LOGGER.error("Failed to get PID: {}", e.getMessage());
        }
        return pid;
    }

    private static void ensureFolder() {
        if (!Files.exists(DIRECTORY)) {
            try {
                HermesMod.LOGGER.info("Creating MCSRHermes/Instances Folder...");
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
