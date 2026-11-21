package me.duncanruns.hermes.mixin.server.instancestate;

import me.duncanruns.hermes.instancestate.InstanceState;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/*!imports
java.util.function.BooleanSupplier
me.duncanruns.hermes.instancestate.InstanceState
net.minecraft.server.MinecraftServer
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.injection.At
org.spongepowered.asm.mixin.injection.Inject
org.spongepowered.asm.mixin.injection.callback.CallbackInfo
*/

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        InstanceState.update((MinecraftServer) (Object) this);
    }
}
