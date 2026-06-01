//? if <=1.12.2 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerManager.class)
public interface PlayerManagerAccessor {
    @Accessor(value = "allowCommands")
    boolean getAllowCommands();
}
*///?}