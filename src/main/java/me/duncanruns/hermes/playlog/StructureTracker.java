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
        structureMap.keySet().removeIf(uuid -> server.getPlayerManager().getPlayer(uuid) == null);
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (player.age % 20 != 0) return;

            //? if <=1.14.4 {
            /*BlockPos blockPos = new BlockPos((float) player.x, (float) player.y, (float) player.z);
            *///?} else if <=1.19.3 {
            BlockPos blockPos = new BlockPos((float) player.getX(), (float) player.getY(), (float) player.getZ());
            //?} else {
            /*BlockPos blockPos = BlockPos.ofFloored(player.getX(), player.getY(), player.getZ());
            *///?}
            ServerWorld world = Util.getPlayerServerWorld(player);

            //?if <=1.14.3 {
            /*if (!world.isHeightValidAndBlockLoaded(blockPos)) return;
            *///?} else if <=1.21.1 {
            if (!world.canSetBlock(blockPos)) return;
            //?} else {
            /*if (!world.isValidForSetBlock(blockPos)) return;
            *///?}

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
        //? if >=1.16
        net.minecraft.world.gen.StructureAccessor structureAccessor = world.getStructureAccessor();

        //? if <=1.18.1 {
        net.minecraft.world.gen.feature.StructureFeature.STRUCTURES.forEach((structureName, feature) -> {
        //?} else if <=1.18.2 {
        /*net.minecraft.util.registry.Registry<net.minecraft.world.gen.feature.ConfiguredStructureFeature<?, ?>> structReg = world.getRegistryManager().get(net.minecraft.util.registry.Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
        structReg.forEach(feature -> {
            String structureName = Objects.requireNonNull(structReg.getId(feature)).toString();
        *///?} else if <=1.19.2 {
        /*net.minecraft.util.registry.Registry<net.minecraft.world.gen.structure.Structure> structReg = world.getRegistryManager().get(net.minecraft.util.registry.Registry.STRUCTURE_KEY);
        structReg.forEach(structureRaw -> {
            net.minecraft.util.registry.RegistryKey<net.minecraft.world.gen.structure.Structure> feature = structReg.getKey(structureRaw).orElseThrow(() -> new RuntimeException("Structure from registry doesn't have a key!?"));
            String structureName = Objects.requireNonNull(structReg.getId(structureRaw)).toString();
        *///?} else if <=1.21.1 {
        /*net.minecraft.registry.Registry<net.minecraft.world.gen.structure.Structure> structReg = world.getRegistryManager().get(net.minecraft.registry.RegistryKeys.STRUCTURE);
        structReg.forEach(structureRaw -> {
            net.minecraft.registry.RegistryKey<net.minecraft.world.gen.structure.Structure> feature = structReg.getKey(structureRaw).orElseThrow(() -> new RuntimeException("Structure from registry doesn't have a key!?"));
            String structureName = Objects.requireNonNull(structReg.getId(structureRaw)).toString();
        *///?} else {
        /*net.minecraft.registry.Registry<net.minecraft.world.gen.structure.Structure> structReg = world.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.STRUCTURE);
        structReg.forEach(structureRaw -> {
            net.minecraft.registry.RegistryKey<net.minecraft.world.gen.structure.Structure> feature = structReg.getKey(structureRaw).orElseThrow(() -> new RuntimeException("Structure from registry doesn't have a key!?"));
            String structureName = Objects.requireNonNull(structReg.getId(structureRaw)).toString();
        *///?}
            //? if <=1.15.2 {
            /*if (!feature.isInsideStructure(world, blockPos)) return;
            *///?} else if <=1.16.1 {
            if (!structureAccessor.method_28388(blockPos, true, feature).hasChildren()) return;
            //?} else if <= 1.17.1 {
            /*if (!structureAccessor.getStructureAt(blockPos, true, feature).hasChildren()) return;
            *///?} else if <=1.20.4 {
            /*if (!structureAccessor.getStructureContaining(blockPos, feature).hasChildren()) return;
            *///?} else {
            /*if (!structureAccessor.getStructureContaining(blockPos, e -> e.matchesKey(feature)).hasChildren()) return;
            *///?}
            structures.add(structureName);
        });
        return structures;
    }
}
