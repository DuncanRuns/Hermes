package me.duncanruns.hermes.mixin.playlog;

import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Optional;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    //? if <=1.19 {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)I", shift = At.Shift.AFTER))
    private void onSuccessfulCommand(ServerCommandSource commandSource, String command, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Integer> cir) {
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayerEntity) entity, command)));
    }
    //?} else if <=1.20.2 {
    /*@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/ParseResults;)I", shift = At.Shift.AFTER))
    private void onSuccessfulCommand(com.mojang.brigadier.ParseResults<ServerCommandSource> parseResults, String command, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Integer> cir) {
        ServerCommandSource commandSource = parseResults.getContext().getSource();
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayerEntity) entity, command)));
    }
    *///?} else {
    /*@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;callWithContext(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void onSuccessfulCommand(com.mojang.brigadier.ParseResults<ServerCommandSource> parseResults, String command, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ServerCommandSource commandSource = parseResults.getContext().getSource();
        MinecraftServer server = getServerFromSource(commandSource);
        Optional.ofNullable(commandSource.getEntity())
                .filter(e -> e instanceof ServerPlayerEntity)
                .ifPresent(entity -> PlayLogHelper.getPlayLog(server).ifPresent(p -> p.onCommand((ServerPlayerEntity) entity, command)));
    }
    *///?}

    @Unique
    private static MinecraftServer getServerFromSource(ServerCommandSource commandSource) {
        //? if <=1.17 {
        return commandSource.getMinecraftServer();
         //?} else {
        /*return commandSource.getServer();
        *///?}
    }
}
