package me.duncanruns.hermes.playlog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.util.Util;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.stream.Collectors;

/*!imports
com.google.gson.Gson
com.google.gson.JsonObject
java.util.stream.Collectors
me.duncanruns.hermes.util.Util
net.minecraft.entity.effect.StatusEffect
net.minecraft.entity.effect.StatusEffectInstance
net.minecraft.registry.Registries
net.minecraft.registry.Registry
net.minecraft.server.MinecraftServer
net.minecraft.util.registry.Registry
*/

public class EffectsTracker {
    private static final Gson GSON = new Gson();
    private final Map<UUID, Map<String, Integer>> effects = new HashMap<>();

    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        effects.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().getPlayer(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            UUID id = Util.getPlayerUUID(player);
            Map<String, Integer> oldEffects = effects.computeIfAbsent(id, uuid -> new HashMap<>());

            // the <= is not missing, it is called MOB_EFFECTS in only 1.14.3
            //? if 1.14.3 {
            /*final Registry<StatusEffect> effectReg = Registry.MOB_EFFECT;
            *///?} else if <=1.19.2 {
            final Registry<StatusEffect> effectReg = Registry.STATUS_EFFECT;
            //?} else if <=1.20.4 {
            /*final Registry<StatusEffect> effectReg = Registries.STATUS_EFFECT;
            *///?}
            //? if <=1.20.4 {
            Map<String, Integer> newEffects = player.getStatusEffects().stream().collect(Collectors.toMap(e -> Objects.requireNonNull(effectReg.getId(e.getEffectType())).toString(), StatusEffectInstance::getAmplifier));
            //?} else {
            /*Map<String, Integer> newEffects = player.getStatusEffects().stream().collect(Collectors.toMap(e -> Objects.requireNonNull(e.getEffectType().getIdAsString()), StatusEffectInstance::getAmplifier));
            *///?}
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
