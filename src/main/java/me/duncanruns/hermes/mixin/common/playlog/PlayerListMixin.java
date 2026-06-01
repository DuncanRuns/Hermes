package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLog;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @SuppressWarnings("LocalMayUseName") // Using local names seems to cause a mixin failure, at least in dev
    @Inject(method = "respawn", at = @At("RETURN"))
    private void onRespawnPlayer(CallbackInfoReturnable<ServerPlayer> cir, @Local(argsOnly = true) ServerPlayer serverPlayer, @Local(argsOnly = true) boolean keepAllPlayerData) {
        PlayLogHelper.getPlayLog(Util.getPlayerServer(serverPlayer)).ifPresent(p -> p.onRespawn(serverPlayer, keepAllPlayerData));
    }

    @Inject(method = "saveAll", at = @At("RETURN"))
    private void onSave(CallbackInfo info) {
        PlayLogHelper.getPlayLog(getServer()).ifPresent(PlayLog::onPlayerDataSave);
    }
}
