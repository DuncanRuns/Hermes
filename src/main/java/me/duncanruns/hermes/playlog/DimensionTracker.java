package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import java.util.*;

// Implementation note: Doing a mixin to changeDimension is just not a good idea, as it isn't called on respawns, and
// sometimes gets spammed when exiting the end. Doing a tracker like this resolves all of those issues and is easier.
public class DimensionTracker {
    Map<UUID, String> dimensions = new HashMap<>();

    /**
     * @return A list of json objects representing the changes.
     */
    public Iterable<JsonObject> tick(MinecraftServer minecraftServer) {
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        dimensions.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().getPlayer(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            UUID id = player.getGameProfile().getId();
            String oldDimension = dimensions.getOrDefault(id, null);
            String newDimension = player.world.getRegistryKey().getValue().toString();
            if (Objects.equals(oldDimension, newDimension)) return;
            dimensions.put(id, newDimension);

            JsonObject data = new JsonObject();
            data.add("player", PlayLog.toPlayerData(player));
            data.addProperty("dimension", newDimension);
            changes.add(data);
        });
        return changes;
    }
}
