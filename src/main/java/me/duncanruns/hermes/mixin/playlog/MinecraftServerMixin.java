package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.playlog.PlayLogServer;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements PlayLogServer {
    @Unique
    private PlayLog playLog;

    @Unique
    private String enteredSeed = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        enteredSeed = EnteredSeedHolder.enteredSeed.get();
        EnteredSeedHolder.enteredSeed.remove();
    }

    @Inject(method = "createWorlds", at = @At("RETURN"))
    private void onFinishCreateWorlds(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        this.playLog = new PlayLog(server);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        playLog.onTick((MinecraftServer) (Object) this);
    }

    @Inject(method = "shutdown", at = @At("RETURN"))
    private void onServerShutdown(CallbackInfo ci) {
        playLog.onServerShutdown();
    }

    @Override
    public PlayLog hermes$getPlayLog() {
        return playLog;
    }

    @Override
    public String hermes$takeEnteredSeed() {
        String seed = enteredSeed;
        enteredSeed = null;
        return seed;
    }
}
