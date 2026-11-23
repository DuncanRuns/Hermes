package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(RandomizableContainer.class)
public interface RandomizableContainerMixin {
    @Inject(method = "unpackLootTable", at = @At("HEAD"))
    private void onLootChest(CallbackInfo ci, @Local(argsOnly = true) Player player) {
        Identifier lootTable = Optional.ofNullable(((RandomizableContainer) this).getLootTable())
                .map(ResourceKey::identifier).orElse(null);
        if (player instanceof ServerPlayer serverPlayer && lootTable != null) {
            PlayLogHelper.getPlayLog(Util.getPlayerServer(serverPlayer)).ifPresent(playLog ->
                    playLog.onLootContainer(serverPlayer, lootTable.toString())
            );
        }
    }
}
