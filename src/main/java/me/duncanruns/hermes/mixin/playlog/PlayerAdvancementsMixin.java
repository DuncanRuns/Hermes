package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

    @Shadow
    private ServerPlayer player;
    @Shadow
    @Final
    private PlayerList playerList;

    @Inject(method = "award", at = @At("RETURN"))
    private void onAdvancement(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = playerList.getServer();
        PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onAdvancement(holder, criterion, getOrStartProgress(holder).isDone(), player));
    }
}
