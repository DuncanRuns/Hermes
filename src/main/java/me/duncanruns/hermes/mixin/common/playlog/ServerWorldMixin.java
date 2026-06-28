package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @SuppressWarnings("all")
    protected ServerWorldMixin() {
        //? if <=1.7.10 {
        /*// For some reason there's two constructors with 5 of the same objects but in a different order
        super(null, null, null, (net.minecraft.world.dimension.Dimension) null, null);
        *///?} else if <=1.13 {
        /*super(null, null, null, null, false);
        *///?} else {
        super(null, null, null, null, null, false);
        //?}
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @Inject(method = "save", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/server/world/chunk/ServerChunkCache;save(Z)Z"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSource;save(ZLnet/minecraft/util/ProgressListener;)Z")
    }, require = 1, allow = 1)
    private void onSave(CallbackInfo ci) {
        //noinspection RedundantCast, DataFlowIssue
        PlayLogHelper.getPlayLog(((ServerWorld) (Object) this).getServer()).ifPresent(p -> p.onWorldSave(Util.getDimensionName((ServerWorld) (Object) this)));
    }
}
