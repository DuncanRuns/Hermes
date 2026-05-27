package me.duncanruns.hermes.playlog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import me.duncanruns.hermes.util.Util;
import net.minecraft.entity.living.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InventoryTracker {
    Map<UUID, List<ItemStack>> inventories = new HashMap<>();

    private static JsonElement stackToJson(ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        //? if <=1.12.2 {
        /*return me.duncanruns.hermes.util.NbtToJson.convert(itemStack.writeNbt(new NbtCompound()));
        *///?} else {
        return com.mojang.datafixers.Dynamic.convert(net.minecraft.nbt.NbtOps.INSTANCE, com.mojang.datafixers.types.JsonOps.INSTANCE, itemStack.writeNbt(new NbtCompound()));
         //?}
    }

    private static boolean areItemListsEqual(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!areItemsEqual(a.get(i), b.get(i))) return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean areItemsEqual(ItemStack a, ItemStack b) {
        if (a.isEmpty() && b.isEmpty()) return true;
        return ItemStack.matchesItemIgnoreDamage(a, b);
    }

    /**
     * @return A list of json objects representing the changes.
     */
    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        inventories.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().get(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerManager().getAll().forEach(player -> {
            UUID id = Util.getPlayerUUID(player);
            PlayerInventory inventory = player.inventory;
            // Note: Putting offhand at the ends means that the order should be the same for older versions of MC
            // 0 -> 35 = main, 36 -> 39 = armor, 40 = offhand
            List<ItemStack> newItems = getInventoryStream(inventory).map(ItemStack::copy).collect(Collectors.toList());
            List<ItemStack> oldItems = inventories.computeIfAbsent(id, uuid -> getEmptyInventory(newItems.size()));
            if (areItemListsEqual(oldItems, newItems)) {
                return;
            }
            inventories.put(id, newItems);

            JsonObject data = new JsonObject();
            data.add("player", PlayLog.toPlayerData(player));
            JsonObject changedSlots = new JsonObject();
            for (int i = 0; i < newItems.size(); i++) {
                if (!areItemsEqual(oldItems.get(i), newItems.get(i))) {
                    changedSlots.add(String.valueOf(i), stackToJson(newItems.get(i)));
                }
            }
            data.add("slots", changedSlots);
            changes.add(data);
        });
        return changes;
    }

    private static Stream<ItemStack> getInventoryStream(PlayerInventory inventory) {
        return HermesMod.concat(inventory.items.stream(), inventory.armor.stream(), inventory.offhand.stream());
    }

    private List<ItemStack> getEmptyInventory(int size) {
        return IntStream.range(0, size).mapToObj(i -> ItemStack.EMPTY).collect(Collectors.toList());
    }
}
