package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    //? if <=1.20.6 {
    @Shadow
    private boolean seenCredits;

    // Targetting seenCredits = true
    @Inject(method = {"changeDimension", "moveToWorld"}, at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;seenCredits:Z", opcode = Opcodes.PUTFIELD), require = 1, allow = 1)
    private void onWinGame(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<?> cir) {
        ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
        PlayLogHelper.getPlayLog(Util.getPlayerServer(thisPlayer)).ifPresent(playLog ->
                playLog.onWinGame(thisPlayer, this.seenCredits)
        );
    }
    //?} else {
    /*@Shadow
    public boolean seenCredits;

    @Inject(method = "detachForDimensionChange", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;seenCredits:Z", opcode = Opcodes.PUTFIELD), require = 1, allow = 1)
    private void onWinGame(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){
        ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;
        PlayLogHelper.getPlayLog(Util.getPlayerServer(thisPlayer)).ifPresent(playLog ->
                playLog.onWinGame(thisPlayer, this.seenCredits)
        );
    }
    *///?}
}
