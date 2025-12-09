package me.duncanruns.hermes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class ClientToServerHelper {
    public static @Nullable MinecraftServer getServer(MinecraftClient client) {
        return client.getServer();
    }
}
