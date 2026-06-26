//? if <=1.8 {
/*package me.duncanruns.hermes.mixin.common;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerManager.class)
public interface PlayerManagerAccessor {
    @SuppressWarnings("rawtypes")
    @Accessor("players")
    List getPlayers();
}
*///?}
