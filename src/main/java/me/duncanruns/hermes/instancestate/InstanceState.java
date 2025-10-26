package me.duncanruns.hermes.instancestate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.Hermes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public final class InstanceState {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static String lastOutput = "{}";
    private static RandomAccessFile file;

    private static final Collection<BiConsumer<JsonObject, MinecraftClient>> CLIENT_STATE_UPDATERS = new CopyOnWriteArrayList<>();
    private static final Collection<BiConsumer<JsonObject, MinecraftServer>> GENERAL_STATE_UPDATERS = new CopyOnWriteArrayList<>();

    private InstanceState() {
    }

    public static void update(MinecraftClient client) {
        assert file != null;
        JsonObject json = new JsonObject();
        CLIENT_STATE_UPDATERS.forEach(u -> u.accept(json, client));
        GENERAL_STATE_UPDATERS.forEach(u -> u.accept(json, client.getServer()));
        output(GSON.toJson(json));
    }

    public static void update(MinecraftServer server) {
        JsonObject json = new JsonObject();
        GENERAL_STATE_UPDATERS.forEach(u -> u.accept(json, server));
        output(GSON.toJson(json));
    }

    private static void output(String newOutput) {
        if (!newOutput.equals(lastOutput)) {
            lastOutput = newOutput;
            final String s = lastOutput;
            EXECUTOR.execute(() -> writeOutput(s));
        }
    }

    private static void writeOutput(String string) {
        try {
            // Stage 1: Old data (valid json)
            file.seek(0);
            file.setLength(0);
            // Stage 2: No Data
            file.write(string.getBytes()); // Stage 3: Partial data (invalid json)
            // Stage 4: Full data (valid json)
        } catch (Exception e) {
            Hermes.LOGGER.error("Failed to write Hermes state: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * The registered updater will be called every time the state is updated, which is every tick and screen change, so
     * be careful with performance.
     */
    public static void registerClientStateUpdater(BiConsumer<JsonObject, MinecraftClient> updater) {
        CLIENT_STATE_UPDATERS.add(updater);
    }

    /**
     * The registered updater will be called every time the state is updated, which is every tick and screen change for
     * the client and every tick for the server, so be careful with performance.
     */
    public static void registerStateUpdater(BiConsumer<JsonObject, MinecraftServer> updater) {
        GENERAL_STATE_UPDATERS.add(updater);
    }

    public static void init() {
        try {
            file = new RandomAccessFile(Hermes.LOCAL_HERMES_FOLDER.resolve("state.json").toFile(), "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Hermes.registerClose(() -> {
            try {
                file.close();
                EXECUTOR.shutdown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        registerStateUpdater((json, server) ->
                json.add("world", Optional.ofNullable(server).map(s -> Hermes.pathToJsonObject(server.getSavePath(WorldSavePath.ROOT).normalize().toAbsolutePath())).orElse(null))
        );
        if (!Hermes.IS_CLIENT) return;
        AtomicReference<Path> lastWorldJoined = new AtomicReference<>(null);
        registerClientStateUpdater((json, client) -> {
            Optional.ofNullable(client.getServer()).map(s -> s.getSavePath(WorldSavePath.ROOT).normalize().toAbsolutePath()).ifPresent(lastWorldJoined::set);
            json.add("screen", Hermes.screenToJsonObject(client.currentScreen));
            json.add("last_world_joined", Hermes.pathToJsonObject(lastWorldJoined.get()));
        });
    }
}
