package me.duncanruns.hermes.playlog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

/**
 * Helper class for accessing the play log.
 */
public final class PlayLogHelper {
    private PlayLogHelper() {
    }

    public static Optional<PlayLog> getPlayLog(MinecraftServer server) {
        return ((PlayLogServer) server).hermes$getPlayLog();
    }

    @Environment(EnvType.CLIENT)
    public static Optional<PlayLog> getCurrentPlayLog() {
        return Optional.ofNullable(MinecraftClient.getInstance())
                .map(MinecraftClient::getServer)
                .flatMap(PlayLogHelper::getPlayLog);
    }
}
