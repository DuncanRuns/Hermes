package me.duncanruns.hermes.mixin.server.instancestate;

import me.duncanruns.hermes.instancestate.InstanceState;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tickServer", at = @At("RETURN"))
    private void onTick(BooleanSupplier haveTime, CallbackInfo ci) {
        InstanceState.update((MinecraftServer) (Object) this);
    }
}
