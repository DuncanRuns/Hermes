package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.SeedCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SeedCommand.class)
public abstract class SeedCommandMixin {
    @WrapOperation(method = "lambda$register$0", at = @At(value = "INVOKE", target = "Ljava/lang/String;valueOf(J)Ljava/lang/String;", ordinal = 0))
    private static String onSlashSeed(long l, Operation<String> original, @Local(argsOnly = true) CommandContext<CommandSourceStack> c) {
        MinecraftServer server = c.getSource().getServer();
        PlayLogHelper.getPlayLog(server).ifPresent(playLog -> playLog.onViewSeed(l));
        return original.call(l);
    }
}
