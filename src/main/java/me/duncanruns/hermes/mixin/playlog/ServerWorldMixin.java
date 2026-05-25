package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
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
        //? if <=1.13 {
        /*super(null, null, null, null, false);
        *///?} else {
        super(null, null, null, null, null, false);
        //?}
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/chunk/ServerChunkCache;save(Z)Z"))
    private void onSave(CallbackInfo ci) {
        String worldName = this.dimension.getType().toString();
        PlayLogHelper.getPlayLog(((ServerWorld) (Object) this).getServer()).ifPresent(p -> p.onWorldSave(worldName));
    }
}
