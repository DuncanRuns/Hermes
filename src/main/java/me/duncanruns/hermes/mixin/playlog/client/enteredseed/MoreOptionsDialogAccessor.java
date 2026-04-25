//? if >=1.16 {
package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.OptionalLong;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {
    //? if <=1.16.1 {
    @Accessor("seedText")
    String getSeedText();
    //?} else {
    /*@Accessor("seed")
    OptionalLong getSeedOpt();
    *///?}
}
//?}