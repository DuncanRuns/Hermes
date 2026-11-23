//? if <=1.20.2 {
package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin {
    @Shadow
    @Nullable
    protected Identifier lootTableId;

    @Inject(method = "checkLootInteraction", at = @At("HEAD"))
    private void onLootChest(CallbackInfo ci, @Local(argsOnly = true) PlayerEntity player) {
        if (player instanceof ServerPlayerEntity && this.lootTableId != null) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            PlayLogHelper.getPlayLog(Util.getPlayerServer(serverPlayer)).ifPresent(playLog ->
                    playLog.onLootContainer(serverPlayer, lootTableId.toString())
            );
        }
    }
}
//?}