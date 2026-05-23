package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.stat.ServerPlayerStats;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerStats.class)
public abstract class ServerPlayerStatsMixin extends PlayerStats {
    @Inject(method = "set", at = @At("HEAD"))
    private void onSetStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity)) return;
        PlayLogHelper.getPlayLog(Util.getPlayerServer((ServerPlayerEntity) player)).ifPresent(p -> p.onStat(this, player, stat, value));
    }
}
