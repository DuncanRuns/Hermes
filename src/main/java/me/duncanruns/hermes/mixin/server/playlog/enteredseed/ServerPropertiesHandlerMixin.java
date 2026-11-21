package me.duncanruns.hermes.mixin.server.playlog.enteredseed;

import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*!imports
me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder
net.minecraft.server.dedicated.AbstractPropertiesHandler
net.minecraft.server.dedicated.ServerPropertiesHandler
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.injection.At
org.spongepowered.asm.mixin.injection.Inject
org.spongepowered.asm.mixin.injection.callback.CallbackInfo
*/

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
