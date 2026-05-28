package me.duncanruns.hermes.mixin.server.playlog.enteredseed;

import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Main.class)
public class MainMixin {
    @Inject(method = "createNewWorldData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelSettings;<init>(Ljava/lang/String;Lnet/minecraft/world/level/GameType;Lnet/minecraft/world/level/LevelSettings$DifficultySettings;ZLnet/minecraft/world/level/WorldDataConfiguration;)V"))
    private static void onCreateNewWorld(CallbackInfoReturnable<?> cir) {
        if (!HermesCore.IS_CLIENT) ServerSeedHolder.serverCreatingNewWorld = true;
    }
}
