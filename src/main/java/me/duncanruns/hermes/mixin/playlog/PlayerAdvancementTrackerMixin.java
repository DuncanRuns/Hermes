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
    //? if <=1.20.1 {
    @Shadow
    public abstract AdvancementProgress getProgress(net.minecraft.advancement.Advancement advancement);
    //?} else {
    /*@Shadow
    public abstract AdvancementProgress getProgress(net.minecraft.advancement.AdvancementEntry advancement);
    *///?}

    @Shadow
    private ServerPlayerEntity owner;
    //? if <=1.15.2 {
    @Shadow
    @Final
    private MinecraftServer server;
    //?} else if <=1.20.1 {
    /*@Shadow
    @Final
    private net.minecraft.server.PlayerManager field_25325;
    *///?} else {
    /*@Shadow
    @Final
    private net.minecraft.server.PlayerManager playerManager;
    *///?}

    //? if <=1.20.1 {
    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void onAdvancement(net.minecraft.advancement.Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        //? if <=1.15.2 {
        MinecraftServer server = this.server;
         //?} else {
        /*MinecraftServer server = field_25325.getServer();
        *///?}
        PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onAdvancement(advancement, criterionName, getProgress(advancement).isDone(), owner));
    }
    //?} else {
    /*@Inject(method = "grantCriterion", at = @At("RETURN"))
    private void onAdvancement(net.minecraft.advancement.AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        PlayLogHelper.getPlayLog(playerManager.getServer()).ifPresent(p -> p.onAdvancement(advancement, criterionName, getProgress(advancement).isDone(), owner));
    }
    *///?}
}
