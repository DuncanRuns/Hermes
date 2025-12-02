package me.duncanruns.hermes.worldlog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public final class WorldLog {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static Path lastWorld = null;
    private static RandomAccessFile file;
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private WorldLog() {
    }

    public static Path init() {
        try {
            Path worldLogsFolder = HermesMod.LOCAL_HERMES_FOLDER.resolve("world_logs");
            if (!Files.exists(worldLogsFolder)) Files.createDirectories(worldLogsFolder);
            String fileName = "worlds_" + System.currentTimeMillis() + ".log";
            Path worldLogPath = worldLogsFolder.resolve(fileName);
            file = new RandomAccessFile(worldLogPath.toFile(), "rw");
            file.seek(0);
            file.setLength(0);
            Files.write(HermesMod.LOCAL_HERMES_FOLDER.resolve("latest_world_log.txt"), fileName.getBytes());
            return worldLogPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(MinecraftClient client) {
        IntegratedServer server = client.getServer();
        Path world = Optional.ofNullable(server).map(s -> HermesMod.getSavePath(server).normalize()).orElse(null);

        Path previousWorld = lastWorld;
        if (!Objects.equals(world, lastWorld)) {
            lastWorld = world;
            write(world == null ? previousWorld : world, (world == null ? "leave" : "entering"), System.currentTimeMillis());
        }
    }

    public static void write(Path worldPath, String type, long time) {
        JsonObject json = new JsonObject();
        json.add("world", HermesMod.pathToJsonObject(worldPath.normalize().toAbsolutePath()));
        json.addProperty("type", type);
        json.addProperty("time", time);
        String jsonString = GSON.toJson(json);
        try {
            EXECUTOR.execute(() -> {
                try {
                    file.write((jsonString + "\n").getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RejectedExecutionException ignored) {
            // Probably shutting down, ignore
        }
    }

}