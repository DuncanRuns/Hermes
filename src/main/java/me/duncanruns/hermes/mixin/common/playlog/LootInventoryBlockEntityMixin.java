//? if >=1.9 {
package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.block.entity.LootInventoryBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootInventoryBlockEntity.class)
public abstract class LootInventoryBlockEntityMixin {
    @Inject(method = "unpackLoot", at = @At("HEAD"))
    private void onLootChest(CallbackInfo ci, @Local(argsOnly = true) PlayerEntity player) {
        Identifier lootTableId = ((LootInventoryBlockEntity) (Object) this).getLootTableId();
        if (player instanceof ServerPlayerEntity && lootTableId != null) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            PlayLogHelper.getPlayLog(Util.getPlayerServer(serverPlayer)).ifPresent(playLog ->
                    playLog.onLootContainer(serverPlayer, lootTableId.toString())
            );
        }
    }
}
//?}