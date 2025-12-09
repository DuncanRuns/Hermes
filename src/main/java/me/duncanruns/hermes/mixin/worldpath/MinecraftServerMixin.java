package me.duncanruns.hermes.mixin.worldpath;

import me.duncanruns.hermes.worldpath.WorldPathHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

/**
 * server.getSavePath, at least in 1.16+, has side effects with computing the resolved path to a map.
 * So to avoid thread safety issues, we yoink the value in the constructor.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements WorldPathHolder {
    @Unique
    private Path worldPath;

    @Unique
    private static Path getSavePath(MinecraftServer server) {
        //? if >=1.16 {
        return server.getSavePath(net.minecraft.util.WorldSavePath.ROOT);
        //?} else {
        /*return server.getLevelStorage().getSavesDirectory().resolve(server.getLevelName());
         *///?}
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        worldPath = getSavePath((MinecraftServer) (Object) this);
    }

    @Override
    public Path hermes$getWorldPath() {
        return worldPath;
    }
}
