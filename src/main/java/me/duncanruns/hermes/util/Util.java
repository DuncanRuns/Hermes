package me.duncanruns.hermes.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class Util {
    private Util() {
    }

    public static UUID getPlayerUUID(Player player) {
        return player.getGameProfile().id();
    }

    public static String getPlayerName(Player player) {
        return player.getGameProfile().name();
    }

    public static ServerLevel getPlayerServerWorld(ServerPlayer player) {
        return player.level();
    }

    public static MinecraftServer getPlayerServer(ServerPlayer player) {
        return player.level().getServer();
    }

    public static Vec3 getEntityPos(Entity entity){
        return entity.position();
    }
}
