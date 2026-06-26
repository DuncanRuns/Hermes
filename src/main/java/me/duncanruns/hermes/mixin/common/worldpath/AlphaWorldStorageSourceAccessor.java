//? if <=1.8.9 {
/*package me.duncanruns.hermes.mixin.common.worldpath;

import net.minecraft.world.storage.AlphaWorldStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(AlphaWorldStorageSource.class)
public interface AlphaWorldStorageSourceAccessor {
    @Accessor("dir")
    File getDir();
}
*///?}