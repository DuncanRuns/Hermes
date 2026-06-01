package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    @SuppressWarnings("all")
    protected ServerLevelMixin() {
        super(null, null, null, null, false, false, 0, 0);
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;save(Z)V"))
    private void onSave(CallbackInfo ci) {
        String worldName = this.dimension().identifier().toString();
        PlayLogHelper.getPlayLog(((ServerLevel) (Object) this).getServer()).ifPresent(p -> p.onWorldSave(worldName));
    }
}
