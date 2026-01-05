package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.advancement.Advancement;
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
    private ServerPlayerEntity owner;
    //? if >=1.16 {
    @Shadow
    @Final
    private net.minecraft.server.PlayerManager field_25325;

    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);
    //?} else {
    /*@Shadow
    @Final
    private MinecraftServer server;
    *///?}

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void onAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        //? if >=1.16 {
        MinecraftServer server = field_25325.getServer();
        //?} else {
        /*MinecraftServer server = this.server;
        *///?}
        PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onAdvancement(advancement, criterionName, getProgress(advancement).isDone(), owner));
    }
}
