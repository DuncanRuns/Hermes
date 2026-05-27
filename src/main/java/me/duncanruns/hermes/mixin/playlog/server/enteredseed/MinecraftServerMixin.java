//? if <=1.15.2 {
/*package me.duncanruns.hermes.mixin.playlog.server.enteredseed;

import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "loadWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelInfo;<init>(JLnet/minecraft/world/GameMode;ZZLnet/minecraft/world/level/LevelGeneratorType;)V"))
    private void onCreateNewWorld(CallbackInfo ci) {
        if (!HermesCore.IS_CLIENT) ServerSeedHolder.serverCreatingNewWorld = true;
    }
}
*///?}