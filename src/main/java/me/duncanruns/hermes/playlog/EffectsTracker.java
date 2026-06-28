package me.duncanruns.hermes.playlog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import java.util.*;
import java.util.stream.Collectors;

public class EffectsTracker {
    private static final Gson GSON = new Gson();
    private final Map<Object, Map<String, Integer>> effects = new HashMap<>();
    //? if >=1.8 <=1.8.9
    //private static final Map<Integer, net.minecraft.resource.Identifier> REVERSE_EFFECT_MAP = me.duncanruns.hermes.mixin.common.playlog.StatusEffectAccessor.getRegistry().entrySet().stream().collect(Collectors.toMap(e -> e.getValue().getId(), Map.Entry::getKey));

    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        effects.keySet().removeIf(id -> Util.getPlayer(minecraftServer, id) == null);
        List<JsonObject> changes = new ArrayList<>();
        Util.getPlayers(minecraftServer).forEach(player -> {
            Object id = Util.getUniquePlayerID(player);
            Map<String, Integer> oldEffects = effects.computeIfAbsent(id, _id -> new HashMap<>());

            //? if >=1.9 <=1.13 {
            /*net.minecraft.util.registry.IdRegistry<net.minecraft.resource.Identifier, net.minecraft.entity.living.effect.StatusEffect> effectReg = net.minecraft.entity.living.effect.StatusEffect.REGISTRY;
            *///?} else if >1.13 {
            final net.minecraft.util.registry.Registry<net.minecraft.entity.living.effect.StatusEffect> effectReg = net.minecraft.util.registry.Registry.STATUS_EFFECT;
            //?}
            //? if <=1.7.10 {
            /*//noinspection unchecked
            Map<String, Integer> newEffects = ((Collection<StatusEffectInstance>)player.getStatusEffects()).stream().collect(Collectors.toMap(StatusEffectInstance::getName, StatusEffectInstance::getAmplifier));
            *///?} else if <=1.8.9 {
            /*//noinspection unchecked
            Map<String, Integer> newEffects = ((Collection<StatusEffectInstance>)player.getStatusEffects()).stream().collect(Collectors.toMap(e -> REVERSE_EFFECT_MAP.get(e.getId()).toString(), StatusEffectInstance::getAmplifier));
            *///?} else {
            Map<String, Integer> newEffects = player.getStatusEffects().stream().collect(Collectors.toMap(e -> Objects.requireNonNull(effectReg.getKey(e.getEffect())).toString(), StatusEffectInstance::getAmplifier));
            //?}
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
