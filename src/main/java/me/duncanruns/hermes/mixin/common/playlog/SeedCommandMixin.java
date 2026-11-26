package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.PlayLogHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.SeedCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SeedCommand.class)
public abstract class SeedCommandMixin {
    //? if <=1.12.2 {
    /*@WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Long;valueOf(J)Ljava/lang/Long;"))
    private Long a(long l, Operation<Long> original, @Local(argsOnly = true) net.minecraft.server.command.source.CommandSource source) {
        @SuppressWarnings({"RedundantCast", "RedundantSuppression"})
        MinecraftServer server = ((net.minecraft.server.world.ServerWorld) source.getCommandSourceWorld()).getServer();
        PlayLogHelper.getPlayLog(server).ifPresent(playLog -> playLog.onViewSeed(l));
        return original.call(l);
    }
    *///?} else {
    @WrapOperation(method = "lambda$register$1", at = @At(value = "INVOKE", target = "Ljava/lang/String;valueOf(J)Ljava/lang/String;", ordinal = 0))
    private static String onSlashSeed(long l, Operation<String> original, @Local(argsOnly = true) com.mojang.brigadier.context.CommandContext<net.minecraft.server.command.source.CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        PlayLogHelper.getPlayLog(server).ifPresent(playLog -> playLog.onViewSeed(l));
        return original.call(l);
    }
    //?}
}
