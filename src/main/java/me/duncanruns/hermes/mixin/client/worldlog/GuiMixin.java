//? if >=26.2 {
/*package me.duncanruns.hermes.mixin.client.worldlog;

import me.duncanruns.hermes.worldlog.WorldLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = {"setScreen"}, at = @At("RETURN"))
    private void onOpenScreen(CallbackInfo ci) {
        WorldLog.update(this.minecraft);
    }
}
*///?}