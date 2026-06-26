//? if <=1.12.2 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandRegistry;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandRegistry.class)
public abstract class CommandRegistryMixin {
    @Inject(method = "run(Lnet/minecraft/server/command/source/CommandSource;Ljava/lang/String;)I", at = @At("RETURN"))
    private void onRunCommand(CallbackInfoReturnable<Integer> cir, @Local(argsOnly = true) CommandSource source, @Local(argsOnly = true) String command) {
        if (!(source instanceof ServerPlayerEntity)) return;
        //? if <=1.8.9 {
        /^MinecraftServer server = ((ServerPlayerEntity) source).server;
        ^///?} else {
        MinecraftServer server = source.getServer();
        //?}
        PlayLogHelper.getPlayLog(server).ifPresent(playLog -> playLog.onCommand((ServerPlayerEntity) source, command));
    }
}
*///?}