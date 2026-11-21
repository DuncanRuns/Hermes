package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*!imports
me.duncanruns.hermes.playlog.PlayLogHelper
me.duncanruns.hermes.util.Util
net.minecraft.entity.player.PlayerEntity
net.minecraft.server.network.ServerPlayerEntity
net.minecraft.stat.ServerStatHandler
net.minecraft.stat.Stat
net.minecraft.stat.StatHandler
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.injection.At
org.spongepowered.asm.mixin.injection.Inject
org.spongepowered.asm.mixin.injection.callback.CallbackInfo
*/

@Mixin(ServerStatHandler.class)
public abstract class ServerStatHandlerMixin extends StatHandler {
    @Inject(method = "setStat", at = @At("HEAD"))
    private void onSetStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity)) return;
        PlayLogHelper.getPlayLog(Util.getPlayerServer((ServerPlayerEntity) player)).ifPresent(p -> p.onStat(this, player, stat, value));
    }
}
