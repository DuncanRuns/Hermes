package me.duncanruns.hermes;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class ClientToServerHelper {
    public static @Nullable MinecraftServer getServer(Minecraft client) {
        return client.getSingleplayerServer();
    }
}
