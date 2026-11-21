package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*!imports
com.llamalad7.mixinextras.injector.wrapoperation.Operation
com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
com.llamalad7.mixinextras.sugar.Local
com.mojang.brigadier.context.CommandContext
me.duncanruns.hermes.playlog.PlayLogHelper
net.minecraft.server.MinecraftServer
net.minecraft.server.command.SeedCommand
net.minecraft.server.command.ServerCommandSource
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.injection.At
*/

@Mixin(SeedCommand.class)
public abstract class SeedCommandMixin {
    @WrapOperation(method = "method_13617", at = @At(value = "INVOKE", target = "Ljava/lang/String;valueOf(J)Ljava/lang/String;", ordinal = 0))
    private static String onSlashSeed(long l, Operation<String> original, @Local(argsOnly = true) CommandContext<ServerCommandSource> context) {
        //? if <=1.17 {
        MinecraftServer server = context.getSource().getMinecraftServer();
        //?} else {
        /*MinecraftServer server = context.getSource().getServer();
        *///?}
        PlayLogHelper.getPlayLog(server).ifPresent(playLog -> playLog.onViewSeed(l));
        return original.call(l);
    }
}
