package me.duncanruns.hermes.instancestate.updaters;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.core.HermesCore;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Instance state field(s) for the current world.
 */
public class WorldStateUpdater implements BiConsumer<JsonObject, MinecraftServer> {
    @Override
    public void accept(JsonObject json, MinecraftServer server) {
        json.add("world", Optional.ofNullable(server).map(s -> HermesCore.pathToJsonObject(HermesMod.getSavePath(s).normalize().toAbsolutePath())).orElse(null));
    }
}
