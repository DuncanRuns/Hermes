package me.duncanruns.hermes.mixin.playlog.client.fastreset;

import me.duncanruns.hermes.playlog.PlayLogOwner;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 1100)
public abstract class MinecraftServerMixin implements PlayLogOwner {
    @Dynamic
    @Inject(method = "fastReset$fastReset", at = @At("HEAD"), remap = false)
    private void onFastReset(CallbackInfo ci) {
        this.hermes$getPlayLog().onFastReset();
    }
}
