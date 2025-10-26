package me.duncanruns.hermes.alive;

import me.duncanruns.hermes.Hermes;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class Alive {
    private static final Path PATH = Hermes.LOCAL_HERMES_FOLDER.resolve("alive").normalize();

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("Hermes-Alive");
        return thread;
    });

    private static RandomAccessFile file = null;
    private static FileLock fileLock = null;
    private static long pid;

    private Alive() {
    }

    public static void init() {
        try {
            pid = Hermes.getProcessId();
        } catch (Exception e) {
            Hermes.LOGGER.error("Failed to get PID: {}", e.getMessage());
            pid = -1;
        }
        EXECUTOR.scheduleAtFixedRate(Alive::tick, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
        Hermes.registerClose(Alive::close);
    }

    private static void tick() {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void tryCreate() {
        if (!Files.isDirectory(Hermes.LOCAL_HERMES_FOLDER.getParent())) return;
        if (!Files.isDirectory(Hermes.LOCAL_HERMES_FOLDER)) {
            try {
                Files.createDirectories(Hermes.LOCAL_HERMES_FOLDER);
            } catch (Exception e) {
                Hermes.LOGGER.error("Failed to create Hermes folder: {}", e.getMessage());
                EXECUTOR.shutdown();
                return;
            }
        }
        try {
            file = new RandomAccessFile(PATH.toFile(), "rw");
            file.setLength(0);
            file.writeLong(pid);
            fileLock = file.getChannel().tryLock(0L, Long.MAX_VALUE, true);
            tickAlive();
        } catch (IOException e) {
            Hermes.LOGGER.error("Failed to create alive file: {}", e.getMessage());
            EXECUTOR.shutdown();
        }
    }

    private static void close() {
        EXECUTOR.shutdownNow();
        if (file != null) {
            try {
                fileLock.release();
                file.close();
                Files.delete(PATH);
            } catch (IOException ignored) {
            }
        }
    }

}
