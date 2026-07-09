package me.duncanruns.hermes.mixin.client.playlog;

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
        Runnable runnable = () -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onScreenChange(client.screen));
        //? if <=1.7.10 {
        /*// Ideally we do play log things on the server thread, but since play logs are already thread safe and 1.8 has
        // no execute or submit method, we will just do this anyway.
        runnable.run();
        *///?} else {
        server.execute(runnable);
        //?}
    }
}
