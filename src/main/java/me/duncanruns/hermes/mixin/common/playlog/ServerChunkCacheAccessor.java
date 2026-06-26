//? if <=1.11.2 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import net.minecraft.server.world.chunk.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerChunkCache.class)
public interface ServerChunkCacheAccessor {
    @Accessor("generator")
    //? if <=1.8.9{
    /^net.minecraft.world.chunk.ChunkSource getChunkGenerator();
    ^///?} else {
    net.minecraft.world.chunk.ChunkGenerator getChunkGenerator();
    //?}
}
*///?}