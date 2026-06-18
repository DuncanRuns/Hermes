//? if >=26.2 {
package me.duncanruns.hermes.mixin.client.instancestate;

import me.duncanruns.hermes.instancestate.InstanceState;
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
        InstanceState.update(this.minecraft);
    }
}
//?}