package me.duncanruns.hermes.playlog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructureTracker {
    private final Map<UUID, Set<String>> structureMap = new HashMap<>();

    public Collection<JsonObject> tick(MinecraftServer server) {
        List<JsonObject> out = new ArrayList<>();
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        structureMap.keySet().removeIf(uuid -> server.getPlayerManager().get(uuid) == null);
        server.getPlayerManager().getAll().forEach(player -> {
            if (player.ticks % 20 != 0) return;

            BlockPos blockPos = new BlockPos((float) player.x, (float) player.y, (float) player.z);
            ServerWorld world = Util.getPlayerServerWorld(player);

            //? if <=1.12.2 {
            /*if (!world.isChunkLoaded(blockPos)) return;
            *///?} else {
            if (!world.isLoaded(blockPos)) return;
             //?}

            UUID id = Util.getPlayerUUID(player);

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
        Set<String> structures = new HashSet<>();

        //? if <=1.12.2 {
        /*me.duncanruns.hermes.util.StructureUtil.getStructureNames().forEach(structureName -> {
            if (!world.getChunkSource().isInsideStructure(world, structureName, blockPos)) return;
            structures.add(structureName);
        });
        *///?} else {
        net.minecraft.world.gen.structure.StructureFeature.STRUCTURES.forEach((structureName, feature) -> {
            if (!feature.isValid(world, blockPos)) return;
            structures.add(structureName);
        });
        //?}
        return structures;
    }
}
