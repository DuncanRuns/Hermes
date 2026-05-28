//? if >=1.16 {
package me.duncanruns.hermes.mixin.server.playlog.enteredseed;

import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Main.class)
public class MainMixin {
    //? if <=1.18.1 {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelInfo;<init>(Ljava/lang/String;Lnet/minecraft/world/GameMode;ZLnet/minecraft/world/Difficulty;ZLnet/minecraft/world/GameRules;Lnet/minecraft/resource/DataPackSettings;)V"))
    private static void onCreateNewWorld(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (!HermesCore.IS_CLIENT) ServerSeedHolder.serverCreatingNewWorld = true;
    }
    //?}  else {
    /*@Inject(method = {"method_40373", "method_43613", "createWorld"}, at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelInfo;<init>(Ljava/lang/String;Lnet/minecraft/world/GameMode;ZLnet/minecraft/world/Difficulty;ZLnet/minecraft/world/GameRules;Lnet/minecraft/resource/DataPackSettings;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelInfo;<init>(Ljava/lang/String;Lnet/minecraft/world/GameMode;ZLnet/minecraft/world/Difficulty;ZLnet/minecraft/world/GameRules;Lnet/minecraft/resource/DataConfiguration;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelInfo;<init>(Ljava/lang/String;Lnet/minecraft/world/GameMode;ZLnet/minecraft/world/Difficulty;ZLnet/minecraft/world/rule/GameRules;Lnet/minecraft/resource/DataConfiguration;)V")
    }, require = 1, allow = 1)
    private static void onCreateNewWorld(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<?> cir) {
        if (!HermesCore.IS_CLIENT) ServerSeedHolder.serverCreatingNewWorld = true;
    }
    *///?}
}
//?}