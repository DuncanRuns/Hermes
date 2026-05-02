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

    @Inject(method = {"openScreen", "setScreen"}, at = @At("RETURN"), require = 1, allow = 1)
    private void onOpenScreen(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        IntegratedServer server = client.getServer();
        if (server == null) return;
        assert client.currentScreen != null;
        Runnable runnable = () -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onScreenChange(client.currentScreen));
        //? if <=1.14 {
        /*server.execute(runnable);
        *///?} else if <=1.14.4 {
        /*server.method_20493(runnable);
         *///?} else {
        server.submit(runnable);
        //?}

    }
}
