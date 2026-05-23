package me.duncanruns.hermes.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public final class Util {
    private Util() {
    }

    public static UUID getPlayerUUID(PlayerEntity player) {
        return player.getGameProfile().getId();
    }

    public static String getPlayerName(PlayerEntity player) {
        return player.getGameProfile().getName();
    }

    public static ServerWorld getPlayerServerWorld(ServerPlayerEntity player) {
        return player.getServerWorld();
    }

    public static MinecraftServer getPlayerServer(ServerPlayerEntity player) {
        return player.server;
    }

    public static Vec3d getEntityPos(Entity entity) {
        return entity.m_1784731();
    }
}
