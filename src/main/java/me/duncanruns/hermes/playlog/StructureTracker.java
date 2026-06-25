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
        //? if <=1.12.2 {
        /*StructureHelper.LENIENT_SEARCH.set(true);
        try {
            return StructureHelper.getStructureNames()
                    .stream()
                    .filter(s -> world.getChunkSource().isInsideStructure(world, s, blockPos))
                    .collect(Collectors.toSet());
        } finally {
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
