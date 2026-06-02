package me.duncanruns.hermes.mixin.server.playlog.enteredseed;

import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "loadWorld",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldSettings;<init>(JLnet/minecraft/world/GameMode;ZZLnet/minecraft/world/gen/WorldGeneratorType;)V"))
    private void onCreateNewWorld(CallbackInfo ci) {
        if (!HermesCore.IS_CLIENT) ServerSeedHolder.serverCreatingNewWorld = true;
    }
}
