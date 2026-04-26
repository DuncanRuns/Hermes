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
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow
    private ServerPlayerEntity owner;
    //? if <=1.15.2 {
    /*@Shadow
    @Final
    private MinecraftServer server;
    *///?} else {
    @Shadow
    @Final
    private net.minecraft.server.PlayerManager field_25325;
    //?}

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    private void onAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        //? if <=1.15.2 {
        /*MinecraftServer server = this.server;
         *///?} else {
        MinecraftServer server = field_25325.getServer();
        //?}
        PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onAdvancement(advancement, criterionName, getProgress(advancement).isDone(), owner));
    }
}
