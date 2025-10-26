package me.duncanruns.hermes.mixin.worldlog.client;

import me.duncanruns.hermes.worldlog.WorldLog;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    /**
     * Note: At least in 1.16.1, openScreen seems to always be run right after the server field in client is updated.
     * This might not be the case in other versions. The update method can run whenever, even on tick, so it should be
     * easily portable even if the screen doesn't happen to change with each change of the integrated server field.
     */
    @Inject(method = "openScreen", at = @At("RETURN"))
    private void onOpenScreen(CallbackInfo ci) {
        WorldLog.update((MinecraftClient) (Object) this);
    }
}
