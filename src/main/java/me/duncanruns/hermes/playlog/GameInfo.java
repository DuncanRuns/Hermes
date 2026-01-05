package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Various information about a save that could be found in level.dat or is related only to runtime information.
 * It's possible that later Minecraft versions would mean more information should be added or that previous versions would mean some information should be removed.
 */
public class GameInfo {
    private static final JsonObject DEFAULT_GAMERULES = gameRulesToJson(new GameRules());
    @SerializedName("cheats_allowed")
    private Boolean cheatsAllowed;
    @SerializedName("open_to_lan")
    private Boolean openToLan;
    @SerializedName("hardcore")
    private Boolean hardcore;
    @SerializedName("difficulty_locked")
    private Boolean difficultyLocked;
    @SerializedName("difficulty")
    private String difficulty;
    @SerializedName("players")
    private List<PlayerInfo> players;
    @SerializedName("default_gamemode")
    private String defaultGamemode;
    @SerializedName("data_packs")
    private List<String> dataPacks;
    @SerializedName("enabled_data_packs")
    private List<String> enabledDataPacks;
    @SerializedName("non_default_game_rules")
    private JsonObject nonDefaultGameRules;

    private GameInfo() {
    }

    public static GameInfo fromServer(MinecraftServer server) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.cheatsAllowed = server.getPlayerManager().areCheatsAllowed();
        gameInfo.openToLan = server.isRemote();
        //? if >=1.16 {
        net.minecraft.world.SaveProperties levelProperties = server.getSaveProperties();
        //?} else {
        /*net.minecraft.world.level.LevelProperties levelProperties = server.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD).getLevelProperties();
        *///?}
        gameInfo.hardcore = levelProperties.isHardcore();
        gameInfo.difficultyLocked = levelProperties.isDifficultyLocked();
        gameInfo.difficulty = levelProperties.getDifficulty().getName();
        gameInfo.players = server.getPlayerManager().getPlayerList().stream().map(p -> {
            PlayerInfo pi = new PlayerInfo();
            pi.gamemode = p.interactionManager.getGameMode().getName();
            pi.name = p.getGameProfile().getName();
            pi.uuid = p.getGameProfile().getId().toString();
            return pi;
        }).collect(Collectors.toList());
        gameInfo.defaultGamemode = server.getDefaultGameMode().getName();
        ResourcePackManager<ResourcePackProfile> dataPackManager = server.getDataPackManager();
        //? if >=1.16 {
        gameInfo.dataPacks = dataPackManager.getNames().stream().sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledNames().stream().sorted().collect(Collectors.toList());
        //?} else {
        /*gameInfo.dataPacks = dataPackManager.getProfiles().stream().map(ResourcePackProfile::getName).sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledProfiles().stream().map(ResourcePackProfile::getName).sorted().collect(Collectors.toList());
        *///?}
        gameInfo.nonDefaultGameRules = getChangedGameRules(server);
        return gameInfo;
    }

    private static JsonObject getChangedGameRules(MinecraftServer server) {
        JsonObject gameRules = gameRulesToJson(server.getGameRules());
        gameRules.entrySet().removeIf(e -> DEFAULT_GAMERULES.has(e.getKey()) && DEFAULT_GAMERULES.get(e.getKey()).equals(e.getValue()));
        return gameRules;
    }

    private static JsonObject gameRulesToJson(GameRules gameRules) {
        //? if >=1.16 {
        return NbtOps.INSTANCE.convertTo(com.mojang.serialization.JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
        //?} else {
        /*return com.mojang.datafixers.Dynamic.convert(NbtOps.INSTANCE, com.mojang.datafixers.types.JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
        *///?}
    }

    public static final class PlayerInfo {
        private String name;
        private String uuid;
        private String gamemode;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            PlayerInfo that = (PlayerInfo) o;
            return Objects.equals(name, that.name) && Objects.equals(uuid, that.uuid) && Objects.equals(gamemode, that.gamemode);
        }

        @Override
        public int hashCode() {
            int result = Objects.hashCode(name);
            result = 31 * result + Objects.hashCode(uuid);
            result = 31 * result + Objects.hashCode(gamemode);
            return result;
        }
    }
}
