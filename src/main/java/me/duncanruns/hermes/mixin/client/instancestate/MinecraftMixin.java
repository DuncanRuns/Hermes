package me.duncanruns.hermes.mixin.client.instancestate;

import me.duncanruns.hermes.instancestate.InstanceState;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        InstanceState.update((Minecraft) (Object) this);
    }

    @Inject(method = {"openScreen"}, at = @At("RETURN"))
    private void onOpenScreen(CallbackInfo ci) {
        InstanceState.update((Minecraft) (Object) this);
    }
}
