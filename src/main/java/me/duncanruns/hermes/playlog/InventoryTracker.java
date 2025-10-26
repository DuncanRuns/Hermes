package me.duncanruns.hermes.playlog;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.Hermes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InventoryTracker {
    Map<UUID, List<ItemStack>> inventories = new HashMap<>();

    /**
     * @return A list of json objects representing the changes.
     */
    public List<JsonObject> tick(MinecraftServer minecraftServer) {
        // Remove players that have left to prevent minor leakage, and mirrors the behavior of a solo player relogging for non host players.
        inventories.keySet().removeIf(uuid -> minecraftServer.getPlayerManager().getPlayer(uuid) == null);
        List<JsonObject> changes = new ArrayList<>();
        minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
            UUID id = player.getGameProfile().getId();
            PlayerInventory inventory = player.inventory;
            // Note: Putting offhand at the ends means that the order should be the same for older versions of MC
            // 0 -> 35 = main, 36 -> 39 = armor, 40 -> 41 = offhand
            List<ItemStack> newItems = Hermes.concat(inventory.main.stream(), inventory.armor.stream(), inventory.offHand.stream()).collect(Collectors.toList());
            List<ItemStack> oldItems = inventories.computeIfAbsent(id, uuid -> getEmptyInventory(newItems.size()));
            if (areItemListsEqual(oldItems, newItems)) {
                return;
            }
            inventories.put(id, newItems.stream().map(ItemStack::copy).collect(Collectors.toList()));

            JsonObject data = new JsonObject();
            data.add("player", PlayLog.toPlayerData(player));
            JsonObject changedSlots = new JsonObject();
            for (int i = 0; i < newItems.size(); i++) {
                if (!ItemStack.areEqual(oldItems.get(i), newItems.get(i))) {
                    changedSlots.addProperty(String.valueOf(i), stackToString(newItems.get(i)));
                }
            }
            data.add("slots", changedSlots);
            changes.add(data);
        });
        return changes;
    }

    private List<ItemStack> getEmptyInventory(int size) {
        return IntStream.range(0, size).mapToObj(i -> ItemStack.EMPTY).collect(Collectors.toList());
    }

    // Porting note: NBT got eradicated in 1.20.5, so getTag() will be different
    private static @NotNull String stackToString(ItemStack itemStack) {
        if (itemStack.isEmpty()) return "";
        return String.format("%d %s%s", itemStack.getCount(), Registry.ITEM.getId(itemStack.getItem()), itemStack.getTag() == null ? "" : itemStack.getTag().toString());
    }

    private static boolean areItemListsEqual(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (!ItemStack.areEqual(a.get(i), b.get(i))) return false;
        }
        return true;
    }
}
