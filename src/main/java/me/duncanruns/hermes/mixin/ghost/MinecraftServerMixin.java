package me.duncanruns.hermes.mixin.ghost;

import me.duncanruns.hermes.ghost.GhostManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private final GhostManager ghostManager = new GhostManager();

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        ghostManager.onTick((MinecraftServer) (Object) this);
    }

    @Inject(method = "save(ZZZ)Z", at = @At("RETURN"))
    private void onSave(CallbackInfoReturnable<Boolean> cir) {
        ghostManager.onSave();
    }
}
