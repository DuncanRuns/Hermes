package me.duncanruns.hermes.mixin.server.playlog.enteredseed;

import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.dedicated.ServerProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerProperties.class)
public abstract class ServerPropertiesMixin {
    @Shadow
    public abstract String getString(String key, String defaultValue);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onServerProperties(CallbackInfo ci) {
        ServerSeedHolder.enteredPropertiesSeed = getString("level-seed", "");
    }
}
