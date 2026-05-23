package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancements;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementTrackerMixin {
    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow
    private ServerPlayerEntity player;
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "award", at = @At("RETURN"))
    private void onAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        PlayLogHelper.getPlayLog(this.server).ifPresent(p -> p.onAdvancement(advancement, criterionName, getProgress(advancement).isComplete(), player));
    }
}
