package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Optional;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Inject(method = "performCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void onSuccessfulCommand(com.mojang.brigadier.ParseResults<CommandSourceStack> parseResults, String command, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        CommandSourceStack commandSource = parseResults.getContext().getSource();
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayer)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayer) entity, command)));
    }

    @Unique
    private static MinecraftServer getServerFromSource(CommandSourceStack commandSource) {
        return commandSource.getServer();
    }
}
