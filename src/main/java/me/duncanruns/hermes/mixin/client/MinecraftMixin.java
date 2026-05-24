package me.duncanruns.hermes.mixin.client;

import me.duncanruns.hermes.HermesMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        HermesMod.close();
    }
}
