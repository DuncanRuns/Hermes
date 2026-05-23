package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.*;

// Implementation note: Doing a mixin to changeDimension is just not a good idea, as it isn't called on respawns, and
// sometimes gets spammed when exiting the end. Doing a tracker like this resolves all of those issues and is easier.
public class DimensionTracker {
    Map<UUID, String> oldDimensions = new HashMap<>();
    Map<UUID, Vec3d> oldPositions = new HashMap<>();

    /**
     * @return A list of json objects representing the changes.
     */
    public Iterable<JsonObject> tick(MinecraftServer minecraftServer) {
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        oldDimensions.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().get(uuid) == null);
        oldPositions.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().get(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerManager().getAll().forEach(player -> {
            UUID id = Util.getPlayerUUID(player);
            Vec3d newPos = Util.getEntityPos(player);
            Vec3d oldPos = oldPositions.put(id, newPos);

            ServerWorld world = Util.getPlayerServerWorld(player);
            String newDimension = world.dimension.getType().toString();

            String oldDimension = oldDimensions.put(id, newDimension);
            if (Objects.equals(oldDimension, newDimension)) return;

            JsonObject data = new JsonObject();
            data.add("player", PlayLog.toPlayerData(player));
            data.addProperty("old_dimension", oldDimension);
            data.add("old_position", oldPos == null ? null : PlayLog.toPositionData(oldPos));
            data.addProperty("dimension", newDimension);
            data.add("position", PlayLog.toPositionData(newPos));
            changes.add(data);
        });
        return changes;
    }
}
