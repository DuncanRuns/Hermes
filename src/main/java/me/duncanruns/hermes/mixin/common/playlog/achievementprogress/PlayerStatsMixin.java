//? if >=1.7 <=1.11.2 {
/*package me.duncanruns.hermes.mixin.common.playlog.achievementprogress;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.achievementprogress.ExtendedStatInfo;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.stat.PlayerStats;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatProgress;
import net.minecraft.util.AchievementProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerStats.class)
public abstract class PlayerStatsMixin {
    @Unique
    private PlayerEntity player;

    @Inject(method = "set", at = @At("RETURN"))
    private void setPlayer(CallbackInfo ci, @Local(argsOnly = true) PlayerEntity player) {
        this.player = player;
    }

    @Inject(method = {"setProgress", "getProgress"}, at = @At("RETURN"))
    private void onSetProgress(CallbackInfoReturnable<StatProgress> cir, @Local(argsOnly = true) Stat stat) {
        StatProgress progress = cir.getReturnValue();
        if (progress instanceof AchievementProgress) {
            ((ExtendedStatInfo) progress).hermes$setInfo(player, stat.key);
        }
    }
}
*///?}