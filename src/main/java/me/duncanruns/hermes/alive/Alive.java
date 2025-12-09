package me.duncanruns.hermes.alive;

import me.duncanruns.hermes.HermesMod;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class Alive {
    private static final Path PATH = HermesMod.LOCAL_HERMES_FOLDER.resolve("alive").normalize();

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("Hermes-Alive");
        return thread;
    });

    private static RandomAccessFile file = null;
    private static long pid;

    private static boolean closing = false;

    private Alive() {
    }

    public static void init() {
        try {
            pid = HermesMod.getProcessId();
        } catch (Exception e) {
            HermesMod.LOGGER.error("Failed to get PID: {}", e.getMessage());
            pid = -1;
        }
        EXECUTOR.scheduleAtFixedRate(Alive::tick, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
        HermesMod.registerClose(Alive::close);
    }

    private static void tick() {
        if (closing) return;
        if (file == null) {
            tryCreate();
        } else {
            tickAlive();
        }
    }

    private static void tickAlive() {
        long now = System.currentTimeMillis();
        try {
            file.seek(8);
            file.writeLong(now);
            file.getChannel().force(false);
        } catch (Exception e) {
            HermesMod.LOGGER.error("Failed to write alive file: {}", e.getMessage());
            close();
        }
    }

    private static void tryCreate() {
        if (!Files.isDirectory(HermesMod.LOCAL_HERMES_FOLDER.getParent())) return;
        if (!Files.isDirectory(HermesMod.LOCAL_HERMES_FOLDER)) {
            try {
                Files.createDirectories(HermesMod.LOCAL_HERMES_FOLDER);
            } catch (Exception e) {
                HermesMod.LOGGER.error("Failed to create Hermes folder: {}", e.getMessage());
                EXECUTOR.shutdown();
                return;
            }
        }
        try {
            file = new RandomAccessFile(PATH.toFile(), "rw");
            file.setLength(0);
            file.writeLong(pid);
            tickAlive();
        } catch (IOException e) {
            HermesMod.LOGGER.error("Failed to create alive file: {}", e.getMessage());
            EXECUTOR.shutdown();
        }
    }

    private static void close() {
        closing = true;
        EXECUTOR.shutdownNow();
        if (file != null) {
            try {
                file.close();
                Files.delete(PATH);
            } catch (IOException e) {
                HermesMod.LOGGER.error("Failed to delete alive file: {}", e.getMessage());
            }
        }
    }

}
