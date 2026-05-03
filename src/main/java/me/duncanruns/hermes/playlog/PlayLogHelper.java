package me.duncanruns.hermes.playlog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

/**
 * Helper class for accessing the play log.
 */
public final class PlayLogHelper {
    private PlayLogHelper() {
    }

    public static Optional<PlayLog> getPlayLog(MinecraftServer server) {
        if (server instanceof PlayLogServer)
            return ((PlayLogServer) server).hermes$getPlayLog();
        return Optional.empty();
    }

    @Environment(EnvType.CLIENT)
    public static Optional<PlayLog> getCurrentPlayLog() {
        //noinspection OptionalOfNullableMisuse
        return Optional.ofNullable(Minecraft.getInstance())
                .map(Minecraft::getSingleplayerServer)
                .flatMap(PlayLogHelper::getPlayLog);
    }
}
