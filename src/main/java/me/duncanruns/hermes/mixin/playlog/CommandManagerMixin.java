//? if >= 1.13 {
package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.source.CommandSourceStack;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    //? if <=1.13 {
    /*@Inject(method = "run", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Ljava/lang/String;Ljava/lang/Object;)I", shift = At.Shift.AFTER))
    *///?} else {
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I", shift = At.Shift.AFTER))
    //?}
    private void onSuccessfulCommand(CommandSourceStack commandSource, String command, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayerEntity) entity, command)));
    }

    @Unique
    private static MinecraftServer getServerFromSource(CommandSourceStack commandSource) {
        return commandSource.getServer();
    }
}
//?}