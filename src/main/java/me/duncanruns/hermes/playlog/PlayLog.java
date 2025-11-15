package me.duncanruns.hermes.playlog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.Hermes;
import me.duncanruns.hermes.modintegration.ModIntegration;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import me.duncanruns.hermes.rot.Rotator;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

public class PlayLog {
    private static final String WARNING = "WARNING: USING CIPHERED DATA DURING THE RUN IS NOT ALLOWED, PLEASE CONTACT THE SPEEDRUN.COM/MC MODERATION TEAM IF YOU WANT TO USE CIPHERED DATA.\n" +
            "\n" +
            "AVERTISSEMENT : L’UTILISATION DE DONNÉES CHIFFRÉES PENDANT L’EXÉCUTION N’EST PAS AUTORISÉE. VEUILLEZ CONTACTER L’ÉQUIPE DE MODÉRATION DE SPEEDRUN.COM/MC SI VOUS SOUHAITEZ UTILISER DES DONNÉES CHIFFRÉES.\n" +
            "\n" +
            "ADVERTENCIA: NO SE PERMITE EL USO DE DATOS CIFRADOS DURANTE LA EJECUCIÓN. POR FAVOR, CONTACTE AL EQUIPO DE MODERACIÓN DE SPEEDRUN.COM/MC SI DESEA UTILIZAR DATOS CIFRADOS.\n" +
            "\n" +
            "WARNUNG: DIE VERWENDUNG VON VERSCHLÜSSELTEN DATEN WÄHREND DES LAUFS IST NICHT ERLAUBT. BITTE KONTAKTIEREN SIE DAS MODERATIONSTEAM VON SPEEDRUN.COM/MC, WENN SIE VERSCHLÜSSELTE DATEN VERWENDEN MÖCHTEN.\n" +
            "\n" +
            "ПОПЕРЕДЖЕННЯ: ВИКОРИСТАННЯ ЗАШИФРОВАНИХ ДАНИХ ПІД ЧАС СПІДРАНУ НЕ ДОЗВОЛЕНО. БУДЬ ЛАСКА, ЗВ’ЯЖІТЬСЯ З МОДЕРАЦІЙНОЮ КОМАНДОЮ SPEEDRUN.COM/MC, ЯКЩО ВИ ХОЧЕТЕ ВИКОРИСТОВУВАТИ ЗАШИФРОВАНІ ДАНІ.\n" +
            "\n" +
            "AVVISO: L’USO DI DATI CIFRATI DURANTE L’ESECUZIONE NON È CONSENTITO. SI PREGA DI CONTATTARE IL TEAM DI MODERAZIONE DI SPEEDRUN.COM/MC SE SI DESIDERA UTILIZZARE DATI CIFRATI.\n" +
            "\n" +
            "AVISO: NÃO É PERMITIDO O USO DE DADOS CIFRADOS DURANTE A EXECUÇÃO. POR FAVOR, ENTRE EM CONTATO COM A EQUIPE DE MODERAÇÃO DE SPEEDRUN.COM/MC SE QUISER UTILIZAR DADOS CIFRADOS.\n" +
            "\n" +
            "ПРЕДУПРЕЖДЕНИЕ: ИСПОЛЬЗОВАНИЕ ЗАШИФРОВАННЫХ ДАННЫХ ВО ВРЕМЯ ЗАПУСКА ЗАПРЕЩЕНО. ЕСЛИ ВЫ ХОТИТЕ ИСПОЛЬЗОВАТЬ ЗАШИФРОВАННЫЕ ДАННЫЕ, ПОЖАЛУЙСТА, СВЯЖИТЕСЬ С МОДЕРАЦИОННОЙ КОМАНДОЙ SPEEDRUN.COM/MC.\n" +
            "\n" +
            "警告：运行过程中不允许使用加密数据，如需使用加密数据，请联系 SPEEDRUN.COM/MC 的管理团队。\n" +
            "\n" +
            "警告：実行中に暗号化されたデータを使用することは許可されていません。暗号化データを使用したい場合は、SPEEDRUN.COM/MC のモデレーションチームに連絡してください。\n" +
            "\n" +
            "경고: 실행 중 암호화된 데이터를 사용하는 것은 허용되지 않습니다. 암호화된 데이터를 사용하려면 SPEEDRUN.COM/MC의 모더레이션 팀에 문의하십시오.";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setName("Hermes-PlayLog");
        return thread;
    });
    private static final Collection<PlayLog> PLAY_LOGS = new ConcurrentLinkedQueue<>();
    private static final Collection<Consumer<MinecraftServer>> INITIALIZATION_CONSUMERS = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    private final Path requiredParent;
    private final Path savePath; // example: .minecraft/saves/Random Speedrun #3/hermes/play.log
    private final Path rtPath; // example: .minecraft/saves/Random Speedrun #3/hermes/restricted/play.log.enc
    private RandomAccessFile rtFile;

    private final InventoryTracker inventoryTracker = new InventoryTracker();
    private final DimensionTracker dimensionTracker = new DimensionTracker();
    private final StructureTracker structureTracker = new StructureTracker();

    private boolean isCreated = false;
    private boolean isClosing = false;
    private final List<String> queuedLines = new ArrayList<>();
    private JsonObject lastScreenData = null;
    private GameInfo lastGameInfo = GameInfo.empty();

    public static final Collection<String> STAT_BLOCK_LIST = new ArrayList<>(Arrays.asList(
            "minecraft.custom:minecraft.play_one_minute",
            "minecraft.custom:minecraft.sneak_time"
    ));
    public static final Collection<String> STAT_SUFFIX_BLOCK_LIST = Collections.singletonList(
            "_one_cm"
    );
    public static final Collection<String> STAT_PREFIX_BLOCK_LIST = Collections.singletonList(
            "minecraft.custom:minecraft.time_since_"
    );

    public PlayLog(MinecraftServer server) {
        Path worldFolder = Hermes.getSavePath(server).normalize();
        this.savePath = worldFolder.resolve("hermes").resolve("play.log");
        this.rtPath = worldFolder.resolve("hermes").resolve("restricted").resolve("play.log.enc");
        this.requiredParent = worldFolder;
        onInitialize(server);
        PLAY_LOGS.add(this);
    }

    private void onInitialize(MinecraftServer server) {
        JsonObject data = new JsonObject();
        data.add("generator_options", getGeneratorOptions(server));
        Optional.ofNullable(((PlayLogServer) server).hermes$takeEnteredSeed()).ifPresent(s -> data.addProperty("entered_seed", s));
        EnteredSeedHolder.enteredSeed.remove();
        data.addProperty("world_time", getTimeOfDay(server));
        if (ModIntegration.HAS_ATUM) data.addProperty("atum_running", ModIntegration.atum$isRunning());
        write("initialize", data);
        INITIALIZATION_CONSUMERS.forEach(c -> c.accept(server));
    }

    private static long getTimeOfDay(MinecraftServer server) {
        //? if >=1.16 {
        return server.getSaveProperties().getMainWorldProperties().getTime();
         //?} else {
        /*return server.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD).getTime();
        *///?}
    }

    public static void registerInitializationEvent(Consumer<MinecraftServer> consumer) {
        INITIALIZATION_CONSUMERS.add(consumer);
    }

    private static JsonElement getGeneratorOptions(MinecraftServer server) {
        //? if >=1.16 {
        JsonElement json = net.minecraft.world.gen.GeneratorOptions.CODEC
                .encode(server.getSaveProperties().getGeneratorOptions(), com.mojang.serialization.JsonOps.INSTANCE, new JsonObject())
                .resultOrPartial(s -> Hermes.LOGGER.warn("Failed to encode generator options: {}", s))
                .orElse(null);
        if (json == null) return null;
        clearSeed(json);
        return json;
    //?} else {
        /*net.minecraft.nbt.CompoundTag generatorOptions = server.getWorld(net.minecraft.world.dimension.DimensionType.OVERWORLD).getLevelProperties().getGeneratorOptions();
        JsonElement json = com.mojang.datafixers.Dynamic.convert(net.minecraft.datafixer.NbtOps.INSTANCE, com.mojang.datafixers.types.JsonOps.INSTANCE, generatorOptions);
        clearSeed(json);
        return json;
        *///?}
    }

    private static void clearSeed(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.remove("seed");
            jsonObject.entrySet().forEach(e -> clearSeed(e.getValue()));
        } else if (jsonElement.isJsonArray()) {
            jsonElement.getAsJsonArray().forEach(PlayLog::clearSeed);
        }
    }

    public void onStat(StatHandler statHandler, PlayerEntity player, Stat<?> stat, int value) {
        String name = stat.getName();
        if (PlayLog.STAT_BLOCK_LIST.contains(name)) {
            return;
        } else if (PlayLog.STAT_SUFFIX_BLOCK_LIST.stream().anyMatch(name::endsWith) || PlayLog.STAT_PREFIX_BLOCK_LIST.stream().anyMatch(name::startsWith)) {
            PlayLog.STAT_BLOCK_LIST.add(name); // Faster to check the next time around
            return;
        }
        int diff = value - statHandler.getStat(stat);
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(player));
        data.addProperty("stat", name);
        data.addProperty("value", value);
        data.addProperty("diff", diff);
        write("stat", data);
    }

    public static @NotNull JsonObject toPlayerData(PlayerEntity player) {
        JsonObject playerJson = new JsonObject();
        playerJson.addProperty("name", player.getGameProfile().getName());
        playerJson.addProperty("uuid", player.getGameProfile().getId().toString());
        return playerJson;
    }

    public static @NotNull JsonObject toPositionData(Vec3d pos) {
        JsonObject posJson = new JsonObject();
        posJson.addProperty("x", pos.x);
        posJson.addProperty("y", pos.y);
        posJson.addProperty("z", pos.z);
        return posJson;
    }

    public static @NotNull JsonObject toPositionData(ChunkPos pos) {
        JsonObject posJson = new JsonObject();
        posJson.addProperty("x", pos.x);
        posJson.addProperty("z", pos.z);
        return posJson;
    }

    /**
     * Implementation should be thread agnostic.
     */
    public void write(String type, JsonObject data) {
        if (isClosing) return;

        long currentTime = System.currentTimeMillis();
        JsonObject lineJson = new JsonObject();
        lineJson.addProperty("time", currentTime);
        lineJson.addProperty("type", type);
        lineJson.add("data", data);
        if (ModIntegration.HAS_SPEEDRUNIGT) lineJson.add("speedrunigt", getSpeedRunIGTObject());

        try {
            EXECUTOR.execute(() -> write(GSON.toJson(lineJson)));
        } catch (RejectedExecutionException ignored) {
            // Probably shutting down, ignore
        }
    }

    private void write(String line) {
        if (isClosing) return;
        if (isCreated) {
            try {
                writeToRTFile(line);
                rtFile.getChannel().force(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        queuedLines.add(line);
        if (Files.isDirectory(this.requiredParent)) {
            try {
                Files.createDirectories(rtPath.getParent());
                rtFile = new RandomAccessFile(rtPath.toFile(), "rw");
                long length = rtFile.length();
                rtFile.seek(length);
                isCreated = true;
                for (String queuedLine : queuedLines) {
                    writeToRTFile(queuedLine);
                    rtFile.getChannel().force(false);
                }
                queuedLines.clear();
                writeWarning();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeWarning() {
        Path path = rtPath.resolveSibling("warning.txt");
        try {
            Files.write(path, WARNING.getBytes());
        } catch (IOException e) {
            Hermes.LOGGER.error("Failed to write warning: {}", e.getMessage());
        }
    }

    private void writeToRTFile(String line) throws IOException {
        rtFile.write(Rotator.ROT_HERMES.rotate(line + "\n").getBytes());
    }

    private static @NotNull JsonObject getSpeedRunIGTObject() {
        JsonObject speedrunigtJson = new JsonObject();
        speedrunigtJson.addProperty("rta", ModIntegration.speedRunIGT$getRTA());
        speedrunigtJson.addProperty("igt", ModIntegration.speedRunIGT$getIGT());
        speedrunigtJson.addProperty("retime", ModIntegration.speedRunIGT$getRetime());
        return speedrunigtJson;
    }

    public void onScreenChange(Screen currentScreen) {
        JsonObject data = Hermes.screenToJsonObject(currentScreen);
        if (Objects.equals(data, lastScreenData)) return;
        lastScreenData = data;
        write("screen", data);
    }

    public void onAdvancement(Advancement advancement, String criterionName, boolean done, ServerPlayerEntity owner) {
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(owner));
        data.addProperty("id", advancement.getId().toString());
        data.addProperty("criterion_name", criterionName);
        data.addProperty("completed", done);
        data.add("display", Optional.ofNullable(advancement.getDisplay()).map(a -> {
            JsonObject display = new JsonObject();
            display.addProperty("hidden", a.isHidden());
            display.addProperty("announce_to_chat", a.shouldAnnounceToChat());
            if (Hermes.IS_CLIENT) display.addProperty("show_toast", a.shouldShowToast());
            return display;
        }).orElse(null));
        write("advancement", data);
    }

    public void onTick(MinecraftServer minecraftServer) {
        checkGameInfo(minecraftServer);
        inventoryTracker.tick(minecraftServer).forEach(jsonObject -> write("inventory_slots", jsonObject));
        dimensionTracker.tick(minecraftServer).forEach(jsonObject -> write("dimension", jsonObject));
        structureTracker.tick(minecraftServer).forEach(jsonObject -> write("inside_structures", jsonObject));
    }

    private void checkGameInfo(MinecraftServer minecraftServer) {
        GameInfo newGameInfo = GameInfo.fromServer(minecraftServer);
        JsonObject difference = newGameInfo.getDifference(lastGameInfo);
        if (difference.size() > 0) {
            lastGameInfo = newGameInfo;
            write("game_info", difference);
        }
    }

    public void onCommand(ServerPlayerEntity player, String input) {
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(player));
        data.addProperty("command", input);
        write("command", data);
    }

    public void onServerShutdown() {
        write("server_shutdown", new JsonObject());
        close();
    }

    private void trySaveUnencrypted() {
        try {
            saveUnencrypted();
        } catch (IOException e) {
            Hermes.LOGGER.error("Failed to save unencrypted play log: {}", e.getMessage());
        }
    }

    private void saveUnencrypted() throws IOException {
        if (!isCreated) return;
        if (!Files.exists(rtPath)) return;

        RandomAccessFile unencryptedFile = new RandomAccessFile(savePath.toFile(), "rw");
        long saveProgress = unencryptedFile.length();
        unencryptedFile.seek(saveProgress);
        rtFile.seek(saveProgress);
        while (rtFile.getFilePointer() < rtFile.length()) {
            String line = rtFile.readLine();
            unencryptedFile.write((Rotator.ROT_HERMES.rotate(line) + "\n").getBytes());
        }
        unencryptedFile.close();
        rtFile.seek(rtFile.length());
    }

    public void close() {
        if (isClosing) return;
        PLAY_LOGS.remove(this);
        write("close", new JsonObject());
        EXECUTOR.execute(this::closeInternal);
    }

    private void closeInternal() {
        if (isClosing) return;
        isClosing = true;
        trySaveUnencrypted();
        if (rtFile != null) {
            try {
                rtFile.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void closeAll() {
        ArrayList<PlayLog> toClose = new ArrayList<>(PLAY_LOGS);
        if (!toClose.isEmpty()) {
            Hermes.LOGGER.info("Closing {} play logs", toClose.size());
            toClose.forEach(PlayLog::close);
        }
        EXECUTOR.shutdown();
    }

    public static void init() {
        Hermes.registerClose(PlayLog::closeAll);
    }

    public void onRespawn(ServerPlayerEntity player, boolean alive) {
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(player));
        data.add("position", toPositionData(player.getPos()));
        data.addProperty("was_alive", alive);
        System.out.println(data);
        write("respawn", data);
    }
}
