package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.JsonOps;
import me.duncanruns.hermes.util.Util;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*!imports
com.google.gson.JsonObject
com.google.gson.annotations.SerializedName
com.mojang.datafixers.Dynamic
com.mojang.datafixers.types.JsonOps
com.mojang.serialization.Codec
com.mojang.serialization.JsonOps
java.util.HashMap
java.util.List
java.util.Map
java.util.Objects
java.util.stream.Collectors
me.duncanruns.hermes.util.Util
net.minecraft.datafixer.NbtOps
net.minecraft.datafixers.NbtOps
net.minecraft.nbt.CompoundTag
net.minecraft.nbt.NbtOps
net.minecraft.resource.ResourcePackContainer
net.minecraft.resource.ResourcePackContainerManager
net.minecraft.resource.ResourcePackManager
net.minecraft.resource.ResourcePackProfile
net.minecraft.resource.featuretoggle.FeatureSet
net.minecraft.server.MinecraftServer
net.minecraft.world.GameRules
net.minecraft.world.SaveProperties
net.minecraft.world.dimension.DimensionType
net.minecraft.world.level.LevelProperties
net.minecraft.world.rule.GameRules
*/

/**
 * Various information about a save that could be found in level.dat or is related only to runtime information.
 * It's possible that later Minecraft versions would mean more information should be added or that previous versions would mean some information should be removed.
 */
public class GameInfo {
    //? if <=1.21.1 {
    private static final JsonObject DEFAULT_GAMERULES = gameRulesToJson(new GameRules());
     //?} else {
    /*private static final Map<FeatureSet, JsonObject> FEATURE_SET_TO_GAMERULES = new HashMap<>();
    *///?}
    //? if >=1.21.11
    //private static final Map<FeatureSet, Codec<GameRules>> FEATURE_SET_TO_CODEC = new HashMap<>();
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
        gameInfo.openToLan = server.isRemote();
        //? if <=1.15.2 {
        /*LevelProperties levelProperties = server.getWorld(DimensionType.OVERWORLD).getLevelProperties();
         *///?} else {
        SaveProperties levelProperties = server.getSaveProperties();
        //?}

        gameInfo.cheatsAllowed = levelProperties.areCommandsAllowed() || server.getPlayerManager().areCheatsAllowed();
        gameInfo.hardcore = levelProperties.isHardcore();
        gameInfo.difficultyLocked = levelProperties.isDifficultyLocked();
        //? if <=1.14.2 {
        /*gameInfo.difficulty = levelProperties.getDifficulty().name();
        *///?} else {
        gameInfo.difficulty = levelProperties.getDifficulty().getName();
        //?}
        gameInfo.players = server.getPlayerManager().getPlayerList().stream().map(p -> {
            PlayerInfo pi = new PlayerInfo();
            //? if <= 1.21.4 {
            pi.gamemode = p.interactionManager.getGameMode().getName();
             //?} else {
            /*pi.gamemode = p.interactionManager.getGameMode().asString();
            *///?}
            pi.name = Util.getPlayerName(p);
            pi.uuid = Util.getPlayerUUID(p).toString();
            return pi;
        }).collect(Collectors.toList());
        //? if <= 1.21.4 {
        gameInfo.defaultGamemode = server.getDefaultGameMode().getName();
         //?} else {
        /*gameInfo.defaultGamemode = server.getDefaultGameMode().asString();
        *///?}
        //? if <=1.14.3 {
        /*ResourcePackContainerManager<ResourcePackContainer> dataPackManager = server.getDataPackContainerManager();
         *///?} else  if <=1.14.4 {
        /*ResourcePackManager<ResourcePackProfile> dataPackManager = server.getDataPackContainerManager();
         *///?} else if <=1.16.1 {
        ResourcePackManager<ResourcePackProfile> dataPackManager = server.getDataPackManager();
         //?} else {
        /*ResourcePackManager dataPackManager = server.getDataPackManager();
        *///?}

        //? if <=1.14.3 {
        /*gameInfo.dataPacks = dataPackManager.getAlphabeticallyOrderedContainers().stream().map(ResourcePackContainer::getName).sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledContainers().stream().map(ResourcePackContainer::getName).sorted().collect(Collectors.toList());
        *///?} else if <=1.15.2 {
        /*gameInfo.dataPacks = dataPackManager.getProfiles().stream().map(ResourcePackProfile::getName).sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledProfiles().stream().map(ResourcePackProfile::getName).sorted().collect(Collectors.toList());
        *///?} else if <=1.20.4 {
        gameInfo.dataPacks = dataPackManager.getNames().stream().sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledNames().stream().sorted().collect(Collectors.toList());
        //?} else {
        /*gameInfo.dataPacks = dataPackManager.getIds().stream().sorted().collect(Collectors.toList());
        gameInfo.enabledDataPacks = dataPackManager.getEnabledIds().stream().sorted().collect(Collectors.toList());
        *///?}
        gameInfo.nonDefaultGameRules = getChangedGameRules(server);
        return gameInfo;
    }

    private static JsonObject getChangedGameRules(MinecraftServer server) {
        //? if <=1.21.10 {
        JsonObject gameRules = gameRulesToJson(server.getGameRules());
         //?} else {
        /*JsonObject gameRules = gameRulesToJson(server.getSaveProperties().getGameRules(), server.getSaveProperties().getEnabledFeatures());
        *///?}
        //? if <=1.21.1 {
        JsonObject defaultGameRules = DEFAULT_GAMERULES;
         //?} else if <=1.21.10 {
        /*JsonObject defaultGameRules = FEATURE_SET_TO_GAMERULES.computeIfAbsent(server.getSaveProperties().getEnabledFeatures(), f -> gameRulesToJson(new GameRules(f)));
        *///?} else {
        /*JsonObject defaultGameRules = FEATURE_SET_TO_GAMERULES.computeIfAbsent(server.getSaveProperties().getEnabledFeatures(), f -> gameRulesToJson(new GameRules(f), f));
        *///?}
        gameRules.entrySet().removeIf(e -> defaultGameRules.has(e.getKey()) && defaultGameRules.get(e.getKey()).equals(e.getValue()));
        return gameRules;
    }

    private static JsonObject gameRulesToJson(
            //? if <=1.21.10 {
            GameRules gameRules
             //?} else {
            /*GameRules gameRules,
            FeatureSet featureSet
            *///?}
    ) {
        //? if <=1.14.2 {
        /*return Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, gameRules.serialize()).getAsJsonObject();
        *///?} else if <=1.14.3 || 1.15 {
        /*return Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
        *///?} else if <=1.15.2 {
        /*return Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
         *///?} else if <=1.16.1 {
        return NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
         //?} else if <=1.21.10 {
        /*return NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, gameRules.toNbt()).getAsJsonObject();
         *///?} else {
        /*return FEATURE_SET_TO_CODEC.computeIfAbsent(featureSet,f -> GameRules.createCodec(featureSet))
                .encodeStart(JsonOps.INSTANCE, gameRules).getOrThrow().getAsJsonObject();
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
