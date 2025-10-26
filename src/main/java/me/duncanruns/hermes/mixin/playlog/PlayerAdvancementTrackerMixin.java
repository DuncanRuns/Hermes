package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void a(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        PlayLogHelper.getPlayLog(owner.getServer()).onAdvancement(advancement, criterionName, getProgress(advancement).isDone(), owner);
    }
}
