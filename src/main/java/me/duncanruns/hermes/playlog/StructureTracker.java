package me.duncanruns.hermes.playlog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class StructureTracker {
    private final Map<Object, Set<String>> structureMap = new HashMap<>();

    public Collection<JsonObject> tick(MinecraftServer server) {
        List<JsonObject> out = new ArrayList<>();
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        structureMap.keySet().removeIf(id -> Util.getPlayer(server, id) == null);
        Util.getPlayers(server).forEach(player -> {
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

            Set<String> structures = getStructures(world, blockPos);

            if (!Objects.equals(structureMap.computeIfAbsent(id, uuid -> Collections.emptySet()), structures)) {
                structureMap.put(id, structures);
                JsonArray structureNames = new JsonArray();
                structures.forEach(structureNames::add);
                JsonObject data = new JsonObject();
                data.add("player", PlayLog.toPlayerData(player));
                data.add("structures", structureNames);
                out.add(data);
            }
        });
        return out;
    }

    private static @NotNull Set<String> getStructures(ServerWorld world, BlockPos blockPos) {
        //? if <=1.12.2 {
        /*//? if >1.7.10
        StructureHelper.LENIENT_SEARCH.set(true);
        //noinspection EmptyFinallyBlock
        try {
            return StructureHelper.getStructureNames()
                    .stream()
                    .filter(s -> StructureHelper.isInsideStructure(world, s, blockPos))
                    .collect(Collectors.toSet());
        } finally {
            //? if >1.7.10
            StructureHelper.LENIENT_SEARCH.set(false);
        }
        *///?} else {
        return net.minecraft.world.gen.structure.StructureFeature.STRUCTURES.entrySet()
                .stream()
                .filter(e -> e.getValue().isValid(world, blockPos))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        //?}
    }
}
