package me.duncanruns.hermes.mixin.playlog.client;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "openScreen", at = @At("RETURN"))
    private void onOpenScreen(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        IntegratedServer server = client.getServer();
        if (server == null) return;
        assert client.currentScreen != null;
        server.submit(() -> PlayLogHelper.getPlayLog(server).onScreenChange(client.currentScreen));
    }
}
