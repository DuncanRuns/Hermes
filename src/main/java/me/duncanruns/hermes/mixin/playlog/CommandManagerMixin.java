package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I", shift = At.Shift.AFTER))
    private void onSuccessfulCommand(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cir) {
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(commandSource.getMinecraftServer()).onCommand((ServerPlayerEntity) entity, command));
    }
}
