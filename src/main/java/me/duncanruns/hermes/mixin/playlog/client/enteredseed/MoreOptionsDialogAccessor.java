//? if >=1.16 {
package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {
    @Accessor("seedText")
    String getSeedText();
}
//?}