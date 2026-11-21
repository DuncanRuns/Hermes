package me.duncanruns.hermes.mixin.client;

import me.duncanruns.hermes.HermesMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*!imports
me.duncanruns.hermes.HermesMod
net.minecraft.client.MinecraftClient
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.injection.At
org.spongepowered.asm.mixin.injection.Inject
org.spongepowered.asm.mixin.injection.callback.CallbackInfo
*/

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        HermesMod.close();
    }
}
