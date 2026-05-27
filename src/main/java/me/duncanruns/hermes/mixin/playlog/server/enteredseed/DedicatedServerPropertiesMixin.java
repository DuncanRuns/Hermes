package me.duncanruns.hermes.mixin.playlog.server.enteredseed;

import me.duncanruns.hermes.playlog.enteredseed.ServerSeedHolder;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin extends Settings<DedicatedServerProperties> {

    public DedicatedServerPropertiesMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
        private void onServerProperties(CallbackInfo ci) {
            ServerSeedHolder.enteredPropertiesSeed = get("level-seed", "");
        }
}
