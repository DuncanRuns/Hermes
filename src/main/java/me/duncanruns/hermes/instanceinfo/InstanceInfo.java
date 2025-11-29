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
    public static final Path GLOBAL_FOLDER = HermesMod.GLOBAL_HERMES_FOLDER.resolve("instances");
    public static final Path LOCAL_FOLDER = HermesMod.LOCAL_HERMES_FOLDER.resolve("instances");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private InstanceInfo() {
    }


    public static void init(Path worldLogPath) {
        ensureGlobalFolder();
        ensureLocalFolder();
        createInstanceInfoFiles(worldLogPath);
    }

    private static void createInstanceInfoFiles(Path worldLogPath) {
        long pid = getPid();
        String instanceJson = getInstanceInfoJson(pid, worldLogPath);
        writeAndLock(getFilePath(GLOBAL_FOLDER, pid), instanceJson);
        writeAndLock(getFilePath(LOCAL_FOLDER, pid), instanceJson);
    }

    private static void writeAndLock(Path path, String contents) {
        try {
            RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
            file.setLength(0);
            file.seek(0);
            file.write(contents.getBytes());
            FileLock fileLock = file.getChannel().tryLock(0L, Long.MAX_VALUE, true);
            HermesMod.registerClose(() -> {
                try {
                    fileLock.release();
                    file.close();
                    Files.delete(path);
                } catch (Throwable ignored) {
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull String getInstanceInfoJson(long pid, Path worldLogPath) {
        JsonObject instanceJson = new JsonObject();
        if (pid != -1) instanceJson.addProperty("pid", pid);
        if (worldLogPath != null) instanceJson.add("world_log", HermesMod.pathToJsonObject(worldLogPath));
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
        return GSON.toJson(instanceJson);
    }

    private static @NotNull Path getFilePath(Path folder, long pid) {
        String fileName;
        if (pid == -1) {
            fileName = "unknown-" + System.currentTimeMillis() + "-" + new Random().nextLong();
        } else {
            fileName = String.valueOf(pid);
        }
        return folder.resolve(fileName + ".json");
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

    private static void ensureGlobalFolder() {
        if (!Files.exists(GLOBAL_FOLDER)) {
            try {
                HermesMod.LOGGER.info("Creating global instances folder...");
                Files.createDirectories(GLOBAL_FOLDER);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void ensureLocalFolder() {
        if (!Files.exists(LOCAL_FOLDER)) {
            try {
                HermesMod.LOGGER.info("Creating local instances folder...");
                Files.createDirectories(LOCAL_FOLDER);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
