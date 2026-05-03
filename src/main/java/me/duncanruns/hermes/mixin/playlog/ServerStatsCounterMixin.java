package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatsCounter.class)
public abstract class ServerStatsCounterMixin extends StatsCounter {
    @Inject(method = "setValue", at = @At("HEAD"))
    private void onSetStat(Player player, Stat<?> stat, int count, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer)) return;
        PlayLogHelper.getPlayLog(Util.getPlayerServer((ServerPlayer) player)).ifPresent(p -> p.onStat(this, player, stat, count));
    }
}
