package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.Codec;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.WorldData;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Various information about a save that could be found in level.dat or is related only to runtime information.
 * It's possible that later Minecraft versions would mean more information should be added or that previous versions would mean some information should be removed.
 */
public class GameInfo {
    private static final Map<FeatureFlagSet, JsonObject> FEATURE_SET_TO_GAMERULES = new java.util.HashMap<>();
    private static final Map<FeatureFlagSet, Codec<GameRules>> FEATURE_SET_TO_CODEC = new java.util.HashMap<>();
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
        gameInfo.cheatsAllowed = server.getPlayerList().isAllowCommandsForAllPlayers();
        gameInfo.openToLan = server.isPublished();
        // "WorldData" is mojang's "LevelProperties"
        WorldData worldData = server.getWorldData();

        gameInfo.hardcore = worldData.isHardcore();
        gameInfo.difficultyLocked = worldData.isDifficultyLocked();
        gameInfo.difficulty = worldData.getDifficulty().getSerializedName();
        gameInfo.players = server.getPlayerList().getPlayers().stream().map(p -> {
            PlayerInfo pi = new PlayerInfo();
            pi.gamemode = p.gameMode.getGameModeForPlayer().getSerializedName();
            pi.name = Util.getPlayerName(p);
            pi.uuid = Util.getPlayerUUID(p).toString();
            return pi;
        }).collect(Collectors.toList());
        gameInfo.defaultGamemode = server.getDefaultGameType().getSerializedName();
        PackRepository dataPackManager = server.getPackRepository();

        gameInfo.dataPacks = dataPackManager.getAvailableIds().stream().sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getSelectedIds().stream().sorted().collect(Collectors.toList());
        gameInfo.nonDefaultGameRules = getChangedGameRules(server);
        return gameInfo;
    }

    private static JsonObject getChangedGameRules(MinecraftServer server) {
        JsonObject gameRules = gameRulesToJson(server.getGameRules(), server.getWorldData().enabledFeatures());
        JsonObject defaultGameRules = FEATURE_SET_TO_GAMERULES.computeIfAbsent(server.getWorldData().enabledFeatures(), f -> gameRulesToJson(new GameRules(f), f));
        gameRules.entrySet().removeIf(e -> defaultGameRules.has(e.getKey()) && defaultGameRules.get(e.getKey()).equals(e.getValue()));
        return gameRules;
    }

    private static JsonObject gameRulesToJson(GameRules gameRules, FeatureFlagSet featureSet) {
        return FEATURE_SET_TO_CODEC.computeIfAbsent(featureSet, _ -> GameRules.codec(featureSet))
                .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, gameRules).getOrThrow().getAsJsonObject();
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
