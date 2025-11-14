package me.duncanruns.hermes.mixin.playlog.client.enteredseed;
//? if >=1.16 {

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {
    @Accessor("seedText")
    String getSeedText();
}

//?} else {
/*public interface MoreOptionsDialogAccessor {
    // TODO: Make it so this isn't needed? Or doesn't get added to the jar?
}
*///?}