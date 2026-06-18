//? if <=26.1.2 {
/*package me.duncanruns.hermes.mixin.client.playlog;

import me.duncanruns.hermes.playlog.PlayLog;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = {"setScreen"}, at = @At("RETURN"))
    private void onOpenScreen(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        PlayLog.Client.onSetScreen(client);
    }
}
*///?}