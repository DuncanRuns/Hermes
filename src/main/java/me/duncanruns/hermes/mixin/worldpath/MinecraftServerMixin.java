package me.duncanruns.hermes.mixin.worldpath;

import me.duncanruns.hermes.worldpath.WorldPathHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.file.Path;

/**
 * In pre 1.14, dedicated servers do not have the world save name initialized until after the init method
 * (NOT constructor), which is unlike 1.14+ where we can get the path at the end of constructor.
 * However, unlike in 1.14+, the world storage's getFile method is thread-safe as it is stateless, so as long as a
 * different thread doesn't try to get the save path before the world save name is initialized, then there should be no
 * problems. Since it's only dedicated servers that delay the setting of world save names (in 1.13), and there are no
 * other threads accessing the save path in that scenario, this should never be an issue.
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements WorldPathHolder {
    @Unique
    private Path worldPath = null;

    @Unique
    private static Path getSavePath(MinecraftServer server) {
        String worldSaveName = server.getWorldSaveName();
        if (worldSaveName == null) {
            throw new IllegalStateException("Attempted to get save path before server finished initializing!");
        }
        return server.getWorldStorageSource().getFile(worldSaveName, ".").toPath();
    }

    @Override
    public Path hermes$getWorldPath() {
        if (worldPath == null) worldPath = getSavePath((MinecraftServer) ((Object) this));
        return worldPath;
    }
}
