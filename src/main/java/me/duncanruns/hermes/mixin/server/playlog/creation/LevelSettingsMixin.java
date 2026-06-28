package me.duncanruns.hermes.mixin.server.playlog.creation;

import me.duncanruns.hermes.playlog.creation.LevelSettingsHolder;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSettings.class)
public class LevelSettingsMixin {
    @Inject(method = {
            "<init>(JLnet/minecraft/world/GameMode;ZZLnet/minecraft/world/gen/WorldGeneratorType;)V",
            "<init>(JLnet/minecraft/world/WorldSettings$GameMode;ZZLnet/minecraft/world/gen/WorldGeneratorType;)V",
            "<init>(JLnet/minecraft/world/WorldSettings__GameMode;ZZLnet/minecraft/world/gen/WorldGeneratorType;)V",
    }, at = @At("RETURN"), require = 1, allow = 1)
    private void onCreateSettings(CallbackInfo ci) {
        LevelSettingsHolder.lastWorldSettings.set(this);
    }
}
