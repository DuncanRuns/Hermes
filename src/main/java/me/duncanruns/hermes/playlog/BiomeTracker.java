package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BiomeTracker {
    Map<Object, String> biomeMap = new HashMap<>();

    /**
     * @return A list of json objects representing the changes.
     */
    public Iterable<JsonObject> tick(MinecraftServer minecraftServer) {
        biomeMap.keySet().removeIf(o -> Util.getPlayer(minecraftServer, o) == null);
        List<JsonObject> out = new ArrayList<>();
        Util.getPlayers(minecraftServer).forEach(player -> {
            if (player.ticks % 20 != 0) return;

            //? if <=1.7.10 {
            /*BlockPos blockPos = new BlockPos(net.minecraft.util.math.MathHelper.floor(player.x), net.minecraft.util.math.MathHelper.floor(player.y), net.minecraft.util.math.MathHelper.floor(player.z));
             *///?} else {
            BlockPos blockPos = new BlockPos((float) player.x, (float) player.y, (float) player.z);
            //?}
            ServerWorld world = Util.getPlayerServerWorld(player);

            //? if <=1.7.10 {
            /*if (!world.isChunkLoaded(blockPos.x, blockPos.y, blockPos.z)) return;
             *///?} else if <=1.12.2 {
            /*if (!world.isChunkLoaded(blockPos)) return;
             *///?} else {
            if (!world.isLoaded(blockPos)) return;
            //?}

            Object id = Util.getUniquePlayerID(player);

            //? if <=1.7.10 {
            /*String biome = world.getBiome(blockPos.x, blockPos.z).name;
            *///?} else if <=1.8.9 {
            /*String biome = world.getBiome(blockPos).name;
            *///?} else if <=1.13 {
            /*String biome = Objects.requireNonNull(net.minecraft.world.biome.Biome.REGISTRY.getKey(world.getBiome(blockPos))).toString();
            *///?} else {
            String biome = Objects.requireNonNull(net.minecraft.util.registry.Registry.BIOME.getKey(world.getBiome(blockPos))).toString();
            //?}

            if (!Objects.equals(biomeMap.computeIfAbsent(id, _id -> ""), biome)) {
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
