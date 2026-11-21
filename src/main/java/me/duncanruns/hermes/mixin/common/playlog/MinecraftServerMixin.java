package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.playlog.PlayLogServer;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/*!imports
com.llamalad7.mixinextras.sugar.Local
java.util.Optional
me.duncanruns.hermes.playlog.PlayLog
me.duncanruns.hermes.playlog.PlayLogServer
me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder
net.minecraft.server.MinecraftServer
net.minecraft.world.level.LevelInfo
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.Unique
org.spongepowered.asm.mixin.injection.At
org.spongepowered.asm.mixin.injection.Inject
org.spongepowered.asm.mixin.injection.callback.CallbackInfo
*/

// Priority increased to ensure any other modded things, including Hermes ghosts, are saved before the play log closes,
// allowing the closing of the play log to be reliably used as an indicator that files can be read.
@Mixin(value = MinecraftServer.class, priority = 1001)
public abstract class MinecraftServerMixin implements PlayLogServer {
    @Unique
    private PlayLog playLog;

    @Unique
    private String enteredSeed = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        enteredSeed = EnteredSeedHolder.enteredSeed.get();
    }

    @Inject(method = "createWorlds", at = @At("RETURN"))
    //? if <=1.15.2 {
    /*private void onFinishCreateWorlds(CallbackInfo ci, @Local(argsOnly = true) LevelInfo levelInfo) {
        this.playLog = new PlayLog((MinecraftServer) (Object) this, levelInfo);
    }
    *///?} else {
    private void onFinishCreateWorlds(CallbackInfo ci) {
        this.playLog = new PlayLog((MinecraftServer) (Object) this, null);
    }
    //?}

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        if (playLog == null) return;
        playLog.onTick((MinecraftServer) (Object) this);
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onServerShuttingDown(CallbackInfo ci) {
        if (playLog == null) return;
        playLog.onServerShuttingDown();
    }

    @Inject(method = "shutdown", at = @At("RETURN"))
    private void onServerShutdown(CallbackInfo ci) {
        if (playLog == null) return;
        playLog.onServerFinishShutdown();
    }

    @Override
    public Optional<PlayLog> hermes$getPlayLog() {
        return Optional.ofNullable(playLog);
    }

    @Override
    public String hermes$takeEnteredSeed() {
        String seed = enteredSeed;
        enteredSeed = null;
        return seed;
    }
}
