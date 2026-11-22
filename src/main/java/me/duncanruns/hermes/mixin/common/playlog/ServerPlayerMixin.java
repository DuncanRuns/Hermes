package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import me.duncanruns.hermes.util.Util;
import net.minecraft.server.level.ServerPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "showEndCredits", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;seenCredits:Z", opcode = Opcodes.PUTFIELD))
    private void onWinGame(CallbackInfo ci) {
        ServerPlayer thisPlayer = (ServerPlayer) (Object) this;
        PlayLogHelper.getPlayLog(Util.getPlayerServer(thisPlayer)).ifPresent(playLog ->
                playLog.onWinGame(thisPlayer)
        );
    }
}
