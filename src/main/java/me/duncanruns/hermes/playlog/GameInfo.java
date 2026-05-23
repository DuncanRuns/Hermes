package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Various information about a save that could be found in level.dat or is related only to runtime information.
 * It's possible that later Minecraft versions would mean more information should be added or that previous versions would mean some information should be removed.
 */
public class GameInfo {
    private static final JsonObject DEFAULT_GAMERULES = gameRulesToJson(new net.minecraft.world.GameRules());
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
        net.minecraft.world.level.LevelProperties levelProperties = server.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD).getLevelProperties();

        gameInfo.hardcore = levelProperties.isHardcore();
        gameInfo.difficultyLocked = levelProperties.isDifficultyLocked();
        gameInfo.difficulty = levelProperties.getDifficulty().getName();
        gameInfo.players = server.getPlayerManager().getPlayerList().stream().map(p -> {
            PlayerInfo pi = new PlayerInfo();
            pi.gamemode = p.interactionManager.getGameMode().getName();
            pi.name = Util.getPlayerName(p);
            pi.uuid = Util.getPlayerUUID(p).toString();
            return pi;
        }).collect(Collectors.toList());
        gameInfo.defaultGamemode = server.getDefaultGameMode().getName();
        net.minecraft.resource.ResourcePackManager<net.minecraft.resource.ResourcePackProfile> dataPackManager = server.getDataPackContainerManager();

        gameInfo.dataPacks = dataPackManager.getProfiles().stream().map(net.minecraft.resource.ResourcePackProfile::getName).sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledProfiles().stream().map(net.minecraft.resource.ResourcePackProfile::getName).sorted().collect(Collectors.toList());
        gameInfo.nonDefaultGameRules = getChangedGameRules(server);
        return gameInfo;
    }

    private static JsonObject getChangedGameRules(MinecraftServer server) {
        JsonObject gameRules = gameRulesToJson(server.getGameRules());
        JsonObject defaultGameRules = DEFAULT_GAMERULES;
        gameRules.entrySet().removeIf(e -> defaultGameRules.has(e.getKey()) && defaultGameRules.get(e.getKey()).equals(e.getValue()));
        return gameRules;
    }

    private static JsonObject gameRulesToJson(
            net.minecraft.world.GameRules gameRules
    ) {
        return com.mojang.datafixers.Dynamic.convert(net.minecraft.datafixer.NbtOps.INSTANCE, com.mojang.datafixers.types.JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
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
