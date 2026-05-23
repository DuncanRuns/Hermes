package me.duncanruns.hermes.mixin.playlog.client;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = {"openScreen"}, at = @At("RETURN"))
    private void onOpenScreen(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        IntegratedServer server = client.getServer();
        if (server == null) return;
        assert client.screen != null;
        Runnable runnable = () -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onScreenChange(client.screen));
        server.submit(runnable);
    }
}
