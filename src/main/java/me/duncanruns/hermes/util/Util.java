package me.duncanruns.hermes.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public final class Util {
    private Util() {
    }

    public static UUID getPlayerUUID(PlayerEntity player) {
        //? if <=1.21.8 {
        return player.getGameProfile().getId();
        //?} else {
        /*return player.getGameProfile().id();
        *///?}
    }

    public static String getPlayerName(PlayerEntity player) {
        //? if <=1.21.8 {
        return player.getGameProfile().getName();
        //?} else {
        /*return player.getGameProfile().name();
        *///?}
    }

    public static ServerWorld getPlayerServerWorld(ServerPlayerEntity player) {
        //? if <=1.17.1 {
        return player.getServerWorld();
        //?} else if <=1.19.4 {
        /*return player.getWorld();
        *///?} else if <=1.21.5 {
        /*return player.getServerWorld();
        *///?} else if <=1.21.8 {
        /*return player.getWorld();
        *///?} else {
        /*return player.getEntityWorld();
        *///?}
    }

    public static MinecraftServer getPlayerServer(ServerPlayerEntity player) {
        //? if <=1.21.5 {
        return player.server;
         //?} else if <=1.21.8 {
        /*return player.getServer();
        *///?} else {
        /*return player.getEntityWorld().getServer();
        *///?}
    }

    public static Vec3d getEntityPos(Entity entity){
        //? if <=1.21.8 {
        return entity.getPos();
        //?} else {
        /*return entity.getEntityPos();
        *///?}
    }
}
