package me.duncanruns.hermes.playlog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.*;
import java.util.stream.Collectors;

public class EffectsTracker {
    private static final Gson GSON = new Gson();
    private final Map<UUID, Map<String, Integer>> effects = new HashMap<>();

    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        effects.keySet().removeIf(uuid -> minecraftServer.getPlayerList().getPlayer(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            UUID id = Util.getPlayerUUID(player);
            Map<String, Integer> oldEffects = effects.computeIfAbsent(id, _ -> new HashMap<>());

            Map<String, Integer> newEffects = player.getActiveEffects().stream().collect(Collectors.toMap(e -> Objects.requireNonNull(e.getEffect().getRegisteredName()), MobEffectInstance::getAmplifier));
            if (oldEffects.equals(newEffects)) return;
            effects.put(id, newEffects);

            JsonObject data = new JsonObject();
            data.add("player", PlayLog.toPlayerData(player));
            data.add("effects", GSON.toJsonTree(newEffects));
            changes.add(data);
        });
        return changes;
    }
}
