package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    //? if <=1.11.2 {
    /*@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V", ordinal = 0))
    //~ if <=1.8.9 'CallbackInfoReturnable<net.minecraft.entity.Entity>' -> 'CallbackInfo'
    private void onWinGame(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.entity.Entity> ci) {
        if (((ServerPlayerEntity) (Object) this).leavingTheEnd) return;
        onWinGame();
    }
    *///?} else {
    @Inject(method = "changeDimension", at = @At(value = "FIELD", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;seenCredits:Z", opcode = org.objectweb.asm.Opcodes.PUTFIELD))
    private void onWinGame(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.entity.Entity> cir) {
        onWinGame();
    }
    //?}


    @Unique
    private void onWinGame() {
        ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
        PlayLogHelper.getPlayLog(Util.getPlayerServer(thisPlayer)).ifPresent(playLog ->
                playLog.onWinGame(thisPlayer)
        );
    }
}
