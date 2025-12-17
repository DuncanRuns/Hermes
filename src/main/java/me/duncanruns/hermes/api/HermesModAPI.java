package me.duncanruns.hermes.api;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.instancestate.InstanceState;
import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.worldlog.WorldLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A collection of methods for other mods to use Hermes' features. If something can be done using this API, it should be
 * done through this API rather than directly accessing Hermes' internals, which are not guaranteed to be stable, while
 * this API should be more stable since it only uses primitives, json, and Minecraft objects.
 */
@SuppressWarnings("unused")
public final class HermesModAPI {
    private HermesModAPI() {
    }

    /**
     * The registered updater will be called every time the state is updated, which is every tick and screen change, so
     * be careful with performance.
     */
    public static void registerClientStateUpdater(BiConsumer<JsonObject, MinecraftClient> updater) {
        InstanceState.registerClientStateUpdater(updater);
    }

    /**
     * The registered updater will be called every time the state is updated, which is every tick and screen change for
     * the client and every tick for the server, so be careful with performance.
     */
    public static void registerGeneralStateUpdater(BiConsumer<JsonObject, MinecraftServer> updater) {
        InstanceState.registerStateUpdater(updater);
    }

    /**
     * Writes to the play log of the given server. Shouldn't throw errors, fails silently in case of any issues.
     */
    public static void writeToPlayLog(MinecraftServer server, String type, JsonObject data) {
        PlayLogHelper.getPlayLog(server).ifPresent(p -> p.write(type, data));
    }

    /**
     * Writes to the play log of the current world, silently ignores if there is no current world.
     */
    @Environment(EnvType.CLIENT)
    public static void writeToCurrentPlayLog(String type, JsonObject data) {
        PlayLogHelper.getCurrentPlayLog().ifPresent(p -> p.write(type, data));
    }

    /**
     * Writes to the world log.
     */
    public static void writeToWorldLog(Path worldPath, String type, long time) {
        WorldLog.write(worldPath, type, time);
    }

    /**
     * Registers a consumer that will be called directly after the `initialize` event of the play log. Can be combined
     * with `writeToPlayLog` to write to the play log for the consumed server.
     */
    public static void registerPlayLogInitializationEvent(Consumer<MinecraftServer> consumer) {
        PlayLog.registerInitializationEvent(consumer);
    }

    /**
     * Gets the cached save path of the given server.
     */
    public static Path getSavePath(MinecraftServer server) {
        return HermesMod.getSavePath(server);
    }
}
