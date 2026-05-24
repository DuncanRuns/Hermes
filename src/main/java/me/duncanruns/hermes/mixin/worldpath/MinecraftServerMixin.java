package me.duncanruns.hermes.mixin.worldpath;

import me.duncanruns.hermes.worldpath.WorldPathHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

/**
 * server.getSavePath, at least in 1.16+, has side effects with computing the resolved path to a map.
 * So to avoid thread safety issues, we yoink the value in the constructor.
 * We apply the mixin to each child class as the world save name is set later in of each child's constructors, which means at the
 * end of the super class's constructor, the world save name is null.
 */
@Mixin({DedicatedServer.class, IntegratedServer.class})
public abstract class MinecraftServerMixin implements WorldPathHolder {
    @Unique
    private Path worldPath;

    @Unique
    private static Path getSavePath(MinecraftServer server) {
        return server.getWorldStorageSource().getFile(server.getWorldSaveName(), ".").toPath();
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
