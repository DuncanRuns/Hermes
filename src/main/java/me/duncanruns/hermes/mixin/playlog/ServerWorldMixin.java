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
        //? if <=1.15.2 {
        /*super(null, null, null, null, false);
        *///?} else if <=1.16.1 {
        super(null, null, null, null, null, false, false, 0);
        //?} else if <=1.18.2 {
        /*super(null, null, null, null, false, false, 0);
        *///?} else if <=1.19.3 {
        /*super(null, null, null, null, false, false, 0, 0);
        *///?} else if <=1.21.1 {
        /*super(null, null, null, null, null, false, false, 0, 0);
        *///?} else {
        /*super(null, null, null, null, false, false, 0, 0);
        *///?}
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;save(Z)V"))
    private void onSave(CallbackInfo ci) {
        //? if <=1.15.2 {
        /*String worldName = this.dimension.getType().toString();
         *///?} else {
        String worldName = this.getRegistryKey().getValue().toString();
        //?}
        PlayLogHelper.getPlayLog(((ServerWorld) (Object) this).getServer()).ifPresent(p -> p.onWorldSave(worldName));
    }
}
