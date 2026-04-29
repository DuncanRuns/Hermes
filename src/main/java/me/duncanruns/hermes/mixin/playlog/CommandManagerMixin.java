package me.duncanruns.hermes.mixin.playlog;

import com.mojang.brigadier.ParseResults;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    //? if <=1.18.2 {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I", shift = At.Shift.AFTER))
    private void onSuccessfulCommand(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayerEntity) entity, command)));
    }
    //?} else {
    /*@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/ParseResults;)I"))
    private void onSuccessfulCommand(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfoReturnable<Integer> cir) {
        ServerCommandSource commandSource = parseResults.getContext().getSource();
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayerEntity) entity, command)));
    }
    *///?}

    @Unique
    private static MinecraftServer getServerFromSource(ServerCommandSource commandSource) {
        //? if <=1.16.5 {
        return commandSource.getMinecraftServer();
         //?} else {
        /*return commandSource.getServer();
        *///?}
    }
}
