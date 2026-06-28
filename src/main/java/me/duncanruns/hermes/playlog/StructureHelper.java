//? if <=1.12.2 {
/*package me.duncanruns.hermes.playlog;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureFeature;

import java.util.*;

public final class StructureHelper {
    private static final Map<Class<? extends StructureFeature>, String> STRUCTURE_CLASS_MAP = new HashMap<>();
    //? if >1.7.10
    public static final ThreadLocal<Boolean> LENIENT_SEARCH = ThreadLocal.withInitial(() -> false);

    public static synchronized Collection<String> getStructureNames() {
        return new HashSet<>(STRUCTURE_CLASS_MAP.values());
    }

    public static synchronized void addStructureName(String structureName, Class<? extends StructureFeature> clazz) {
        STRUCTURE_CLASS_MAP.put(clazz, structureName);
    }

    //? if <=1.11.2 {
    /^private static final Map<Class<? extends net.minecraft.world.chunk./^¹? if <=1.8.9 {¹^//^¹ChunkSource¹^//^¹?} else {¹^/ChunkGenerator/^¹?}¹^/>, Map<String, java.lang.reflect.Field>> STRUCTURE_FIELD_MAPS = new HashMap<>();

    private static Map<String, java.lang.reflect.Field> getStructureFields(Class<? extends net.minecraft.world.chunk./^¹? if <=1.8.9 {¹^//^¹ChunkSource¹^//^¹?} else {¹^/ChunkGenerator/^¹?}¹^/> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> STRUCTURE_CLASS_MAP.containsKey(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(java.util.stream.Collectors.toMap(f -> STRUCTURE_CLASS_MAP.get(f.getType()), f -> f));
    }

    private static Optional<java.lang.reflect.Field> getStructureFromChunkGenerator(String structureName, net.minecraft.world.chunk./^¹? if <=1.8.9 {¹^//^¹ChunkSource¹^//^¹?} else {¹^/ChunkGenerator/^¹?}¹^/ chunkGenerator) {
        return Optional.ofNullable(STRUCTURE_FIELD_MAPS
                .computeIfAbsent(chunkGenerator.getClass(), StructureHelper::getStructureFields)
                .getOrDefault(structureName, null));
    }

    public static boolean isInsideStructure(ServerWorld world, String structureName, BlockPos pos) {
        net.minecraft.world.chunk./^¹? if <=1.8.9 {¹^//^¹ChunkSource¹^//^¹?} else {¹^/ChunkGenerator/^¹?}¹^/ chunkGenerator = ((me.duncanruns.hermes.mixin.common.playlog.ServerChunkCacheAccessor) world.getChunkSource()).getChunkGenerator();
        return getStructureFromChunkGenerator(structureName, chunkGenerator).map(f -> {
            try {
                //? if <=1.7.10 {
                /^¹return ((StructureFeature) f.get(chunkGenerator)).isInside(pos.x, pos.y, pos.z);
                ¹^///?} else {
                return ((StructureFeature) f.get(chunkGenerator)).isInside(pos);
                //?}
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).orElse(false);
    }
    ^///?} else {
    public static boolean isInsideStructure(ServerWorld world, String structureName, BlockPos pos) {
        return world.getChunkSource().isInsideStructure(world, structureName, pos);
    }
    //?}
}
*///?}