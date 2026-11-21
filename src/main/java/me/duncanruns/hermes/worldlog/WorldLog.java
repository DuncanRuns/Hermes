package me.duncanruns.hermes.worldlog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.modintegration.ModIntegration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/*!imports
com.google.gson.Gson
com.google.gson.GsonBuilder
com.google.gson.JsonObject
java.io.IOException
java.io.RandomAccessFile
java.nio.charset.StandardCharsets
java.nio.file.Files
java.nio.file.Path
java.util.Objects
java.util.Optional
java.util.concurrent.ExecutorService
java.util.concurrent.Executors
java.util.concurrent.RejectedExecutionException
me.duncanruns.hermes.HermesMod
me.duncanruns.hermes.core.HermesCore
me.duncanruns.hermes.modintegration.ModIntegration
net.minecraft.client.MinecraftClient
net.minecraft.server.integrated.IntegratedServer
*/

public final class WorldLog {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static Path lastWorld = null;
    private static RandomAccessFile file;
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private WorldLog() {
    }

    public static Path init() {
        try {
            Path worldLogsFolder = HermesCore.LOCAL_HERMES_FOLDER.resolve("world_logs");
            if (!Files.exists(worldLogsFolder)) Files.createDirectories(worldLogsFolder);
            String fileName = "worlds_" + System.currentTimeMillis() + ".log";
            Path worldLogPath = worldLogsFolder.resolve(fileName);
            file = new RandomAccessFile(worldLogPath.toFile(), "rw");
            file.seek(0);
            file.setLength(0);
            Files.write(HermesCore.LOCAL_HERMES_FOLDER.resolve("latest_world_log.txt"), fileName.getBytes(StandardCharsets.UTF_8));
            HermesMod.registerClose(WorldLog::close);
            return worldLogPath;
        } catch (IOException e) {
            HermesMod.LOGGER.error("Failed to initialize WorldLog!");
            throw new RuntimeException(e);
        }
    }

    private static void close() {
        try {
            EXECUTOR.execute(() -> {
                try {
                    file.close();
                } catch (IOException e) {
                    HermesMod.LOGGER.error("Failed to close play log: {}", e.getMessage());
                }
            });
            EXECUTOR.shutdown();
        } catch (Exception ignored) {
        }
    }

    public static void update(MinecraftClient client) {
        IntegratedServer server = client.getServer();
        Path world = Optional.ofNullable(server).map(s -> HermesMod.getSavePath(server).normalize()).orElse(null);

        Path previousWorld = lastWorld;
        if (!Objects.equals(world, lastWorld)) {
            lastWorld = world;
            if (previousWorld != null) write(previousWorld, "leave", System.currentTimeMillis());
            if (world != null) write(world, "entering", System.currentTimeMillis());
        }
    }

    public static void write(Path worldPath, String type, long time) {
        JsonObject json = new JsonObject();
        json.add("world", HermesCore.pathToJsonObject(worldPath.normalize().toAbsolutePath()));
        json.addProperty("type", type);
        json.addProperty("time", time);
        if (ModIntegration.INTEGRATE_ATUM) json.addProperty("atum_running", ModIntegration.atum$isRunning());
        String jsonString = GSON.toJson(json);
        try {
            EXECUTOR.execute(() -> {
                try {
                    file.write((jsonString + "\n").getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    HermesMod.LOGGER.error("Failed to write world log!", e);
                    close();
                }
            });
        } catch (RejectedExecutionException ignored) {
            // Probably shutting down, ignore
        }
    }

}