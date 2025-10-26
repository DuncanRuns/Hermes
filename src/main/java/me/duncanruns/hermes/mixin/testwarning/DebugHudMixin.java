package me.duncanruns.hermes.mixin.testwarning;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    // TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE TODO REMOVE
    @Inject(method = {"getRightText", "getLeftText"}, at = @At("RETURN"))
    private void a(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().add("YOU ARE USING HERMES, WHICH IS NOT LEGAL");
        cir.getReturnValue().add("YOU ARE USING HERMES, WHICH IS NOT LEGAL");
        cir.getReturnValue().add("YOU ARE USING HERMES, WHICH IS NOT LEGAL");
    }
}
