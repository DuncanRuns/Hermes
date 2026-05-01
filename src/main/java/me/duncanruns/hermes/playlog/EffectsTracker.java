package me.duncanruns.hermes.playlog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;

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

            //? if <=1.14.3 {
            /*final net.minecraft.util.registry.Registry<StatusEffect> effectReg = net.minecraft.util.registry.Registry.MOB_EFFECT;
            *///?} else if <=1.19.2 {
            final net.minecraft.util.registry.Registry<StatusEffect> effectReg = net.minecraft.util.registry.Registry.STATUS_EFFECT;
            //?} else {
            /*final net.minecraft.registry.Registry<StatusEffect> effectReg = net.minecraft.registry.Registries.STATUS_EFFECT;
            *///?}
            Map<String, Integer> newEffects = player.getStatusEffects().stream().collect(Collectors.toMap(e -> Objects.requireNonNull(effectReg.getId(e.getEffectType())).toString(), StatusEffectInstance::getAmplifier));
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
