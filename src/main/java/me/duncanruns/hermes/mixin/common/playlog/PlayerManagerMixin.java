package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(method = "respawn", at = @At("RETURN"))
    private void onRespawnPlayer(CallbackInfoReturnable<ServerPlayerEntity> cir, @Local(argsOnly = true) ServerPlayerEntity player, @Local(argsOnly = true) boolean alive) {
        PlayLogHelper.getPlayLog(Util.getPlayerServer(player)).ifPresent(p -> p.onRespawn(player, alive));
    }

    @Inject(method = "saveAll", at = @At("RETURN"))
    private void onSave(CallbackInfo info) {
        PlayLogHelper.getPlayLog(getServer()).ifPresent(PlayLog::onPlayerDataSave);
    }
}
