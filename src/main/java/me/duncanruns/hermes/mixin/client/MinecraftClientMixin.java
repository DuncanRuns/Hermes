package me.duncanruns.hermes.mixin.client;

import me.duncanruns.hermes.Hermes;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        Hermes.close();
    }
}
