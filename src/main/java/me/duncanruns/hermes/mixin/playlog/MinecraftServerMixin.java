package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.playlog.PlayLogOwner;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements PlayLogOwner {
    @Unique
    private PlayLog playLog;


    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        this.playLog = new PlayLog(server);
    }

    @Override
    public PlayLog hermes$getPlayLog() {
        return playLog;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        playLog.onTick((MinecraftServer) (Object) this);
    }

    @Inject(method = "shutdown", at = @At("RETURN"))
    private void onServerShutdown(CallbackInfo ci) {
        playLog.onServerShutdown();
    }
}
