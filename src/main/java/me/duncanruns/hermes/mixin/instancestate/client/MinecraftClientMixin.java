package me.duncanruns.hermes.mixin.instancestate.client;

import me.duncanruns.hermes.instancestate.InstanceState;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        InstanceState.update((MinecraftClient) (Object) this);
    }

    @Inject(method = {"openScreen", "setScreen"}, at = @At("RETURN"), require = 1, allow = 1)
    private void onOpenScreen(CallbackInfo ci) {
        InstanceState.update((MinecraftClient) (Object) this);
    }
}
