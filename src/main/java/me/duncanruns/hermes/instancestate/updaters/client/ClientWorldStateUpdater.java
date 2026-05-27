package me.duncanruns.hermes.instancestate.updaters.client;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.ClientToServerHelper;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.core.HermesCore;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * Instance state fields related to a singleplayer worlds that don't apply to dedicated servers.
 */
public class ClientWorldStateUpdater implements BiConsumer<JsonObject, Minecraft> {
    private static final AtomicReference<Path> lastWorldJoined = new AtomicReference<>(null);

    @Override
    public void accept(JsonObject json, Minecraft client) {
        MinecraftServer server = ClientToServerHelper.getServer(client);
        Optional.ofNullable(server).map(s -> HermesMod.getSavePath(s).normalize().toAbsolutePath()).ifPresent(lastWorldJoined::set);
        json.add("last_world_joined", HermesCore.pathToJsonObject(lastWorldJoined.get()));
        json.addProperty("open_to_lan", Optional.ofNullable(server).map(s -> (!HermesCore.IS_CLIENT) || ((IntegratedServer) s).isPublished()).orElse(null));
    }
}
