package me.duncanruns.hermes.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.Dimension;

import java.util.List;
import java.util.UUID;

public final class Util {
    private Util() {
    }

    public static /*? if <=1.7.5 {*/ /*String *//*?} else {*/ UUID /*?}*/ getPlayerUUID(PlayerEntity player) {
        return player.getGameProfile().getId();
    }

    public static String getPlayerName(PlayerEntity player) {
        return player.getGameProfile().getName();
    }

    /**
     * Gets an object that uniquely identifies a player and allows for grabbing player entity objects from the server.
     * In pre 1.8, this is the player's name as a String, otherwise is a UUID.
     */
    public static Object getUniquePlayerID(PlayerEntity player) {
        //? if <=1.7.10 {
        /*return getPlayerName(player);
        *///?} else {
        return getPlayerUUID(player);
         //?}
    }

    public static ServerWorld getPlayerServerWorld(ServerPlayerEntity player) {
        return player.getServerWorld();
    }

    public static MinecraftServer getPlayerServer(ServerPlayerEntity player) {
        return player.server;
    }

    public static Vec3d getEntityPos(Entity entity) {
        //? if <=1.7.10 {
        /*return Vec3d.of(entity.x, entity.y, entity.z);
        *///?} else if <=1.12.2 {
        /*return new Vec3d(entity.x, entity.y, entity.z);
         *///?} else {
        return entity.getSourcePos();
         //?}
    }

    public static ServerWorld getOverworld(MinecraftServer server) {
        //? if <=1.8.9 {
        /*return server.getWorld(0);
        *///?} else if <=1.12.2 {
        /*return server.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD.getId());
         *///?} else {
        return server.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD);
         //?}
    }

    @SuppressWarnings({"RedundantSuppression", "unchecked", "RedundantCast"})
    public static List<ServerPlayerEntity> getPlayers(MinecraftServer server) {
        //? if <=1.8 {
        /*return ((me.duncanruns.hermes.mixin.common.PlayerManagerAccessor) server.getPlayerManager()).getPlayers();
        *///?} else {
        return server.getPlayerManager().getAll();
         //?}
    }

    public static ServerPlayerEntity getPlayer(MinecraftServer minecraftServer, Object id) {
        //? if <=1.7.10 {
        /*return minecraftServer.getPlayerManager().get((String) id);
        *///?} else {
        return minecraftServer.getPlayerManager().get((UUID) id);
         //?}
    }

    public static String getDimensionName(ServerWorld world) {
        return getDimensionName(world.dimension);
    }

    public static String getDimensionName(Dimension dimension) {
        //? if <=1.8.9 {
        /*return dimension.getName();
        *///?} else {
        return dimension.getType().toString();
         //?}
    }
}
