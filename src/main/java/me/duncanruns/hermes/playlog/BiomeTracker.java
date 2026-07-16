package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BiomeTracker {
    Map<UUID, String> biomeMap = new HashMap<>();

    /**
     * @return A list of json objects representing the changes.
     */
    public Iterable<JsonObject> tick(MinecraftServer minecraftServer) {

        biomeMap.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().getPlayer(uuid) == null);
        List<JsonObject> out = new ArrayList<>();
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            if (player.age % 20 != 0) return;

            //? if <=1.14.4 {
            /*BlockPos blockPos = new BlockPos((float) player.x, (float) player.y, (float) player.z);
            *///?} else if <=1.19.3 {
            BlockPos blockPos = new BlockPos((float) player.getX(), (float) player.getY(), (float) player.getZ());
            //?} else {
            /*BlockPos blockPos = BlockPos.ofFloored(player.getX(), player.getY(), player.getZ());
            *///?}
            ServerWorld world = Util.getPlayerServerWorld(player);

            //? if <=1.14.3 {
            /*if (!world.isHeightValidAndBlockLoaded(blockPos)) return;
            *///?} else if <=1.21.1 {
            if (!world.canSetBlock(blockPos)) return;
            //?} else {
            /*if (!world.isValidForSetBlock(blockPos)) return;
            *///?}

            UUID id = Util.getPlayerUUID(player);

            //? if <=1.16.1 {
            String biome = Objects.requireNonNull(net.minecraft.util.registry.Registry.BIOME.getId(world.getBiome(blockPos))).toString();
            //?} else if <=1.16.4 {
            /*String biome = world.method_31081(blockPos).orElseThrow(() -> new RuntimeException("Biome at loaded position does not exist?")).getValue().toString();
            *///?} else if <=1.18.1 {
            /*String biome = world.getBiomeKey(blockPos).orElseThrow(() -> new RuntimeException("Biome at loaded position does not exist?")).getValue().toString();
            *///?} else {
            /*String biome = world.getBiome(blockPos).getKey().orElseThrow().getValue().toString();
            *///?}

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
