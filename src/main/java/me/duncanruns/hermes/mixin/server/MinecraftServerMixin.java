package me.duncanruns.hermes.mixin.server;

import me.duncanruns.hermes.Hermes;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "shutdown", at = @At("RETURN"))
    private void onShutdown(CallbackInfo ci) {
        Hermes.close();
    }
}
