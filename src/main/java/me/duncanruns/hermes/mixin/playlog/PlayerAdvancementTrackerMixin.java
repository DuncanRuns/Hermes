package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    public abstract AdvancementProgress getProgress(net.minecraft.advancement.Advancement advancement);

    @Shadow
    private ServerPlayerEntity owner;
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void onAdvancement(net.minecraft.advancement.Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        PlayLogHelper.getPlayLog(this.server).ifPresent(p -> p.onAdvancement(advancement, criterionName, getProgress(advancement).isDone(), owner));
    }
}
