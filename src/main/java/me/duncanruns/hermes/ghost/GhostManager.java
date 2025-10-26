package me.duncanruns.hermes.ghost;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GhostManager implements AutoCloseable {
    private final Map<UUID, GhostWriter> writers = new HashMap<>();

    public void onTick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        server.getPlayerManager().getPlayerList().forEach(player ->
                writers.computeIfAbsent(player.getGameProfile().getId(), uuid -> new GhostWriter(server, uuid)).onTick(time, player)
        );
    }

    public void onSave() {
        writers.values().forEach(GhostWriter::onSave);
    }

    @Override
    public void close() {
        writers.values().forEach(GhostWriter::close);
    }
}
