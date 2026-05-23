//? if >=1.16 <=1.19.3 {
/*package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.OptionalLong;

@Mixin(MoreOptionsDialog.class)
public interface MoreOptionsDialogAccessor {
    //? if <=1.16.1 {
    @Accessor("seedText")
    String getSeedText();
    //?} else if <= 1.16.4 {
    /^@Accessor(value = "seedText")
    OptionalLong getSeedOpt();
    ^///?} else {
    /^@Accessor(value = "seed")
    OptionalLong getSeedOpt();
    ^///?}
}
*///?}