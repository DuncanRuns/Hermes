package me.duncanruns.hermes.mixin.playlog.server.enteredseed;

import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPropertiesHandler.class)
public abstract class ServerPropertiesHandlerMixin extends AbstractPropertiesHandler<ServerPropertiesHandler> {
    public ServerPropertiesHandlerMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onServerProperties(CallbackInfo ci) {
        ServerSeedHolder.enteredPropertiesSeed = getString("level-seed", "");
    }
}
