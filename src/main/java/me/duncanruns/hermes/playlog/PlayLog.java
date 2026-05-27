package me.duncanruns.hermes.playlog;

import com.google.gson.*;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.core.HermesCore;
import me.duncanruns.hermes.modintegration.ModIntegration;
import me.duncanruns.hermes.rot.Rotator;
import me.duncanruns.hermes.util.Util;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
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
    private final EffectsTracker effectsTracker = new EffectsTracker();
    private final SRIGTOptionsTracker srigtOptionsTracker = new SRIGTOptionsTracker();

    private boolean isCreated = false;
    private boolean isClosing = false;
    private final List<String> queuedLines = new ArrayList<>();
    private JsonObject lastScreenData = null;
    private JsonObject lastGameInfo = null;

    private boolean serverShuttingDown = false;
    private final List<String> shutdownWorldSaves = new ArrayList<>();
    private boolean shutdownPlayersSaved = false;

    public static final Collection<String> STAT_BLOCK_LIST = new ArrayList<>(Arrays.asList(
            "minecraft.custom:minecraft.play_one_minute",
            "minecraft.custom:minecraft.sneak_time",
            "minecraft.custom:minecraft.total_world_time",
            "minecraft.custom:minecraft.play_time",
            "stat.playOneMinute",
            "stat.timeSinceDeath"
    ));
    public static final Collection<String> STAT_SUFFIX_BLOCK_LIST = Arrays.asList(
            "_one_cm",
            "OneCm",
            "Time"
    );
    public static final Collection<String> STAT_PREFIX_BLOCK_LIST = Collections.singletonList(
            "minecraft.custom:minecraft.time_since_"
    );

    public PlayLog(MinecraftServer server) {
        Path worldFolder = HermesMod.getSavePath(server).normalize();
        this.savePath = worldFolder.resolve("hermes").resolve("play.log");
        this.rtPath = worldFolder.resolve("hermes").resolve("restricted").resolve("play.log.enc");
        this.requiredParent = worldFolder;
        onInitialize(server);
        PLAY_LOGS.add(this);
    }

    private static long getTime(MinecraftServer server) {
        return getOverworld(server).getTime();
    }

    public static void registerInitializationEvent(Consumer<MinecraftServer> consumer) {
        INITIALIZATION_CONSUMERS.add(consumer);
    }

    private static ServerWorld getOverworld(MinecraftServer server) {
        //? if <=1.12.2 {
        /*return server.getWorld(DimensionType.OVERWORLD.getId());
        *///?} else {
        return server.getWorld(DimensionType.OVERWORLD);
         //?}
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

    public static JsonObject toPlayerData(PlayerEntity player) {
        if (player == null) return null;
        JsonObject playerJson = new JsonObject();
        playerJson.addProperty("name", Util.getPlayerName(player));
        playerJson.addProperty("uuid", Util.getPlayerUUID(player).toString());
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

    private static @NotNull JsonObject getSpeedRunIGTObject() {
        JsonObject speedrunigtJson = new JsonObject();
        speedrunigtJson.addProperty("rta", ModIntegration.speedRunIGT$getRTA());
        speedrunigtJson.addProperty("igt", ModIntegration.speedRunIGT$getIGT());
        speedrunigtJson.addProperty("retime", ModIntegration.speedRunIGT$getRetime());
        return speedrunigtJson;
    }

    public static void closeAll() {
        ArrayList<PlayLog> toClose = new ArrayList<>(PLAY_LOGS);
        if (!toClose.isEmpty()) {
            HermesMod.LOGGER.info("Closing {} play logs", toClose.size());
            toClose.forEach(PlayLog::close);
        }
        EXECUTOR.shutdown();
    }

    public static void init() {
        HermesMod.registerClose(PlayLog::closeAll);
    }

    private void onInitialize(MinecraftServer server) {
        JsonObject data = new JsonObject();
        data.addProperty("hermes_version", HermesMod.VERSION);
        data.addProperty("mc_version", HermesMod.GAME_VERSION);
        // TODO: level_settings
        Optional.ofNullable(((PlayLogServer) server).hermes$takeEnteredSeed()).ifPresent(s -> data.addProperty("entered_seed", s));
        data.addProperty("world_time", getTime(server));
        if (ModIntegration.INTEGRATE_ATUM) data.addProperty("atum_running", ModIntegration.atum$isRunning());
        write("initialize", data);
        INITIALIZATION_CONSUMERS.forEach(c -> c.accept(server));
    }

    public void onStat(PlayerStats statHandler, PlayerEntity player, Stat /*?if >1.12.2 {*/<?>/*?}*/ stat, int value) {
        //? if <= 1.12.2 {
        /*String name = stat.key;
        *///?} else {
        String name = stat.getName();
         //?}
        if (PlayLog.STAT_BLOCK_LIST.contains(name)) {
            return;
        } else if (PlayLog.STAT_SUFFIX_BLOCK_LIST.stream().anyMatch(name::endsWith) || PlayLog.STAT_PREFIX_BLOCK_LIST.stream().anyMatch(name::startsWith)) {
            PlayLog.STAT_BLOCK_LIST.add(name); // Faster to check the next time around
            return;
        }
        int diff = value - statHandler.get(stat);
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(player));
        data.addProperty("stat", name);
        data.addProperty("value", value);
        data.addProperty("diff", diff);
        write("stat", data);
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
        if (ModIntegration.INTEGRATE_SPEEDRUNIGT) lineJson.add("speedrunigt", getSpeedRunIGTObject());

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
                HermesMod.LOGGER.error("Failed to write to play log: {}", e.getMessage());
                closeInternal();
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
                HermesMod.LOGGER.error("Failed to create play log: {}", e.getMessage());
                closeInternal();
            }
        }
    }

    private void writeWarning() {
        Path path = rtPath.resolveSibling("warning.txt");
        try {
            Files.write(path, WARNING.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            HermesMod.LOGGER.error("Failed to write warning: {}", e.getMessage());
        }
    }

    private void writeToRTFile(String line) throws IOException {
        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        Rotator.ROT_HERMES.rotateAndHalfReverse(bytes);
        rtFile.write(bytes);
        rtFile.write('\n');
    }

    public void onScreenChange(Screen currentScreen) {
        JsonObject data = HermesMod.screenToJsonObject(currentScreen);
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
        data.add("display", Optional.ofNullable(advancement.getInfo()).map(a -> {
            JsonObject display = new JsonObject();
            display.addProperty("hidden", a.isHidden());
            display.addProperty("announce_to_chat", a.showInChat());
            if (HermesCore.IS_CLIENT) display.addProperty("show_toast", a.showInToast());
            return display;
        }).orElse(null));
        write("advancement", data);
    }

    public void onTick(MinecraftServer minecraftServer) {
        checkGameInfo(minecraftServer);
        inventoryTracker.tick(minecraftServer).forEach(jsonObject -> write("inventory_slots", jsonObject));
        dimensionTracker.tick(minecraftServer).forEach(jsonObject -> write("dimension", jsonObject));
        structureTracker.tick(minecraftServer).forEach(jsonObject -> write("inside_structures", jsonObject));
        effectsTracker.tick(minecraftServer).forEach(jsonObject -> write("status_effects", jsonObject));
        srigtOptionsTracker.tick().ifPresent(jsonObject -> write("speedrunigt_options", jsonObject));
    }

    private void checkGameInfo(MinecraftServer minecraftServer) {
        JsonObject newGameInfo = GSON.toJsonTree(GameInfo.fromServer(minecraftServer)).getAsJsonObject();
        JsonObject difference = lastGameInfo == null ? newGameInfo : HermesMod.getJsonDifference(lastGameInfo, newGameInfo);
        if (difference.size() > 0) {
            lastGameInfo = newGameInfo;
            write("game_info", difference);
        }
    }

    public void onCommand(ServerPlayerEntity player, String command) {
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(player));
        data.addProperty("command", command);
        write("command", data);
    }

    public void onServerShuttingDown() {
        serverShuttingDown = true;
    }

    public void onServerFinishShutdown() {
        JsonObject data = new JsonObject();
        data.add("worlds_saved", GSON.toJsonTree(shutdownWorldSaves));
        data.addProperty("players_saved", shutdownPlayersSaved);
        write("server_shutdown", data);
        close();
    }

    private void trySaveUnencrypted() {
        try {
            saveUnencrypted();
        } catch (IOException e) {
            HermesMod.LOGGER.error("Failed to save unencrypted play log: {}", e.getMessage());
        }
    }

    private void saveUnencrypted() throws IOException {
        if (!isCreated) return;
        if (!Files.exists(rtPath)) return;

        try (RandomAccessFile unencryptedFile = new RandomAccessFile(savePath.toFile(), "rw")) {
            long saveProgress = unencryptedFile.length();
            long fileLength = rtFile.length();

            if (saveProgress >= fileLength) return;

            rtFile.seek(saveProgress);
            unencryptedFile.seek(saveProgress);

            byte[] readBuffer = new byte[64 * 1024];
            byte[] lineBuffer = new byte[8192];
            int linePos = 0;

            int bytesRead;
            while ((bytesRead = rtFile.read(readBuffer)) != -1) {

                for (int i = 0; i < bytesRead; i++) {
                    byte b = readBuffer[i];

                    if (b == '\n') {
                        Rotator.ROT_HERMES.rotateAndHalfReverse(lineBuffer, linePos);
                        unencryptedFile.write(lineBuffer, 0, linePos);
                        unencryptedFile.write('\n');
                        linePos = 0;
                    } else {
                        if (linePos == lineBuffer.length) {
                            lineBuffer = Arrays.copyOf(lineBuffer, lineBuffer.length * 2);
                        }
                        lineBuffer[linePos++] = b;
                    }
                }
            }

            if (linePos > 0) {
                Rotator.ROT_HERMES.rotateAndHalfReverse(lineBuffer, linePos);
                unencryptedFile.write(lineBuffer, 0, linePos);
            }
        }

        rtFile.seek(rtFile.length());
    }

    public void close() {
        PLAY_LOGS.remove(this);
        if (isClosing) return;
        write("close", new JsonObject());
        try {
            EXECUTOR.execute(this::closeInternal);
        } catch (RejectedExecutionException ignored) {
            // Probably shutting down, ignore
        }
    }

    private void closeInternal() {
        if (isClosing) return;
        isClosing = true;
        PLAY_LOGS.remove(this);
        trySaveUnencrypted();
        if (rtFile != null) {
            try {
                rtFile.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onRespawn(ServerPlayerEntity player, boolean alive) {
        JsonObject data = new JsonObject();
        data.add("player", toPlayerData(player));
        data.add("position", toPositionData(Util.getEntityPos(player)));
        data.addProperty("was_alive", alive);
        write("respawn", data);
    }

    public void onWorldSave(String string) {
        JsonObject data = new JsonObject();
        data.addProperty("world", string);
        write("world_saved", data);
        if (serverShuttingDown) shutdownWorldSaves.add(string);
    }

    public void onPlayerDataSave() {
        write("players_saved", new JsonObject());
        if (serverShuttingDown) shutdownPlayersSaved = true;
    }
}
