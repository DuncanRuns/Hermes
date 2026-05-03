package me.duncanruns.hermes.playlog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructureTracker {
    private final Map<UUID, Set<String>> structureMap = new HashMap<>();

    public Collection<JsonObject> tick(MinecraftServer server) {
        List<JsonObject> out = new ArrayList<>();
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        structureMap.keySet().removeIf(uuid -> server.getPlayerList().getPlayer(uuid) == null);
        server.getPlayerList().getPlayers().forEach(player -> {
            if (player.tickCount % 20 != 0) return;

            BlockPos blockPos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
            ServerLevel world = Util.getPlayerServerWorld(player);

            if (!world.ensureCanWrite(blockPos)) return;

            UUID id = Util.getPlayerUUID(player);

            Set<String> structures = getStructures(world, blockPos);

            if (!Objects.equals(structureMap.computeIfAbsent(id, _ -> Collections.emptySet()), structures)) {
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

    private static @NotNull Set<String> getStructures(ServerLevel world, BlockPos blockPos) {
        Set<String> structures = new HashSet<>();
        StructureManager structureManager = world.structureManager();

        Registry<Structure> structReg = world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        structReg.forEach(structureRaw -> {
            ResourceKey<Structure> feature = structReg.getResourceKey(structureRaw).orElseThrow(() -> new RuntimeException("Structure from registry doesn't have a key!?"));
            String structureName = Objects.requireNonNull(structReg.getKey(structureRaw)).toString();
            if (!structureManager.getStructureWithPieceAt(blockPos, e -> e.is(feature)).isValid()) return;
            structures.add(structureName);
        });
        return structures;
    }
}
