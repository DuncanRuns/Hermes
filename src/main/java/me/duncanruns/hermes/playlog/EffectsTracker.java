package me.duncanruns.hermes.playlog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.stream.Collectors;

public class EffectsTracker {
    private static final Gson GSON = new Gson();
    private final Map<UUID, Map<String, Integer>> effects = new HashMap<>();

    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        effects.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().getPlayer(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            UUID id = player.getGameProfile().getId();
            Map<String, Integer> oldEffects = effects.computeIfAbsent(id, uuid -> new HashMap<>());

            Map<String, Integer> newEffects = player.getStatusEffects().stream().collect(Collectors.toMap(e -> Objects.requireNonNull(Registry.STATUS_EFFECT.getId(e.getEffectType())).toString(), StatusEffectInstance::getAmplifier));
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
