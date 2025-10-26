package me.duncanruns.hermes.mixin.instancestate.server;

import me.duncanruns.hermes.instancestate.InstanceState;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "tick",at=@At("RETURN"))
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        InstanceState.update((MinecraftServer) (Object) this);
    }
}
