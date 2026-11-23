//? if >1.20.2 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LootableInventory.class)
public interface LootableInventoryMixin {
    @Inject(method = "generateLoot", at = @At("HEAD"))
    private void onLootChest(CallbackInfo ci, @Local(argsOnly = true) PlayerEntity player) {
        //? if <=1.20.4 {
        Identifier lootTable = ((LootableInventory) this).getLootTableId();
        //?} else {
        /^Identifier lootTable = Optional.ofNullable(((LootableInventory) this).getLootTable())
                .map(RegistryKey::getValue).orElse(null);
        ^///?}
        if (player instanceof ServerPlayerEntity serverPlayer && lootTable != null) {
            PlayLogHelper.getPlayLog(Util.getPlayerServer(serverPlayer)).ifPresent(playLog ->
                    playLog.onLootContainer(serverPlayer, lootTable.toString())
            );
        }
    }
}
*///?}