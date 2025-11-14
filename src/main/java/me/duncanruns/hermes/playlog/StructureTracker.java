package me.duncanruns.hermes.playlog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.*;

public class StructureTracker {
    private final Map<UUID, Set<String>> structureMap = new HashMap<>();

    public Collection<JsonObject> tick(MinecraftServer server) {
        List<JsonObject> out = new ArrayList<>();
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        structureMap.keySet().removeIf(uuid -> server.getPlayerManager().getPlayer(uuid) == null);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (player.age % 20 != 0) return;

            // The following line to get block pos may look weird, but is how the game does it for advancements (after a few levels of abstraction)
            BlockPos blockPos = new BlockPos((float) player.getX(), (float) player.getY(), (float) player.getZ());
            ServerWorld world = player.getServerWorld();
            if (!world.canSetBlock(blockPos)) return;

            UUID id = player.getGameProfile().getId();

            Set<String> structures = new HashSet<>();
            //? if >=1.16
             net.minecraft.world.gen.StructureAccessor structureAccessor = world.getStructureAccessor();
            StructureFeature.STRUCTURES.forEach((structureName, feature) -> {
                //? if >=1.16 {
                net.minecraft.structure.StructureStart<?> structureStart = structureAccessor.method_28388(blockPos, true, feature);
                if (!structureStart.hasChildren()) return;
                //?} else {
                /*if (!feature.isInsideStructure(world, blockPos)) return;
                *///?}
                structures.add(structureName);
            });

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
}
