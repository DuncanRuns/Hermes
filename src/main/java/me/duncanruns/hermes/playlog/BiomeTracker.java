package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class BiomeTracker {
    Map<UUID, String> biomeMap = new HashMap<>();

    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        biomeMap.keySet().removeIf(uuid -> minecraftServer.getPlayerList().getPlayer(uuid) == null);
        List<JsonObject> out = new ArrayList<>();
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player.tickCount % 20 != 0) return;

            BlockPos blockPos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
            ServerLevel world = Util.getPlayerServerWorld(player);

            if (!world.ensureCanWrite(blockPos)) return;

            UUID id = Util.getPlayerUUID(player);

            String biome = world.getBiome(blockPos).getRegisteredName();

            if (!Objects.equals(biomeMap.computeIfAbsent(id, _ -> ""), biome)) {
                biomeMap.put(id, biome);
                JsonObject data = new JsonObject();
                data.add("player", PlayLog.toPlayerData(player));
                data.addProperty("biome", biome);
                out.add(data);
            }
        });
        return out;
    }
}
