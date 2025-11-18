package me.duncanruns.hermes.mixin.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract MinecraftServer getServer();

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void onRespawnPlayer(CallbackInfoReturnable<ServerPlayerEntity> cir, @Local(argsOnly = true) ServerPlayerEntity player, @Local(argsOnly = true) boolean alive) {
        PlayLogHelper.getPlayLog(player.server).onRespawn(player, alive);
    }

    @Inject(method = "saveAllPlayerData", at = @At("RETURN"))
    private void onSave(CallbackInfo info) {
        PlayLogHelper.getPlayLog(getServer()).onPlayerDataSave();
    }
}
