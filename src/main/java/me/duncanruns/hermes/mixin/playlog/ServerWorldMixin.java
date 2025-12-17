package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    //? if >=1.16 {
    protected ServerWorldMixin(net.minecraft.world.MutableWorldProperties mutableWorldProperties, net.minecraft.util.registry.RegistryKey<World> registryKey, net.minecraft.util.registry.RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, java.util.function.Supplier<Profiler> profiler, boolean bl, boolean bl2, long l) {
        super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
    }
    //?} else {
    /*protected ServerWorldMixin(net.minecraft.world.level.LevelProperties levelProperties, DimensionType dimensionType, java.util.function.BiFunction<World, net.minecraft.world.dimension.Dimension, net.minecraft.world.chunk.ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient) {
        super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
    }
    *///?}

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;save(Z)V"))
    private void onSave(CallbackInfo ci) {
        //? if >=1.16 {
        String worldName = this.getRegistryKey().getValue().toString();
        //?} else {
        /*String worldName = this.dimension.getType().toString();
         *///?}
        PlayLogHelper.getPlayLog(((ServerWorld) (Object) this).getServer()).ifPresent(p -> p.onWorldSave(worldName));
    }
}
