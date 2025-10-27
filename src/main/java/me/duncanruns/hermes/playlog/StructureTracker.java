package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.*;

// *sigh* what a mess
public class StructureTracker {
    private final Map<UUID, Map<String, Set<ChunkPos>>> visitedStructurePositionsMap = new HashMap<>();

    public Collection<JsonObject> tick(MinecraftServer server) {
        List<JsonObject> out = new ArrayList<>();
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        visitedStructurePositionsMap.keySet().removeIf(uuid -> server.getPlayerManager().getPlayer(uuid) == null);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (player.age % 20 != 0) return;

            // The following line to get block pos may look weird, but is how the game does it for advancements (after a few levels of abstraction)
            BlockPos blockPos = new BlockPos((float) player.getX(), (float) player.getY(), (float) player.getZ());
            ServerWorld world = player.getServerWorld();
            if (!world.canSetBlock(blockPos)) return;

            UUID id = player.getGameProfile().getId();
            Map<String, Set<ChunkPos>> map = visitedStructurePositionsMap.computeIfAbsent(id, uuid -> new HashMap<>());
            StructureAccessor structureAccessor = world.getStructureAccessor();

            StructureFeature.STRUCTURES.forEach((structureName, feature) -> {
                StructureStart<?> structureStart = structureAccessor.method_28388(blockPos, true, feature);
                if (!structureStart.hasChildren()) return;

                ChunkPos chunkPos = new ChunkPos(structureStart.getPos());
                Set<ChunkPos> seenPositions = map.computeIfAbsent(structureName, s -> new HashSet<>());
                if (seenPositions.add(chunkPos)) {
                    JsonObject data = new JsonObject();
                    data.add("player", PlayLog.toPlayerData(player));
                    data.addProperty("structure", structureName);
                    data.add("chunk_pos", PlayLog.toPositionData(chunkPos));
                    out.add(data);
                }
            });
        });
        return out;
    }
}
