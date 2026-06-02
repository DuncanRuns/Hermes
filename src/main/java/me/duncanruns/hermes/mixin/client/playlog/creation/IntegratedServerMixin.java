package me.duncanruns.hermes.mixin.client.playlog.creation;

import me.duncanruns.hermes.playlog.creation.LevelSettingsHolder;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Shadow
    @Final
    private WorldSettings settings;

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void onStartLoadWorld(CallbackInfo ci) {
        LevelSettingsHolder.lastWorldSettings.set(this.settings);
    }
}
