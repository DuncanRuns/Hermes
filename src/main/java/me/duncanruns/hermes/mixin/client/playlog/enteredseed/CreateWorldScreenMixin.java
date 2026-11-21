package me.duncanruns.hermes.mixin.client.playlog.enteredseed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/*!imports
com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
com.llamalad7.mixinextras.injector.wrapoperation.Operation
java.util.OptionalLong
me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder
net.minecraft.client.gui.Screen
net.minecraft.client.gui.menu.NewLevelScreen
net.minecraft.client.gui.screen.Screen
net.minecraft.client.gui.screen.world.CreateWorldScreen
net.minecraft.client.gui.screen.world.MoreOptionsDialog
net.minecraft.client.gui.screen.world.WorldCreator
org.spongepowered.asm.mixin.Final
org.spongepowered.asm.mixin.Mixin
org.spongepowered.asm.mixin.Shadow
org.spongepowered.asm.mixin.Unique
*/

//? if <=1.14 {
/*@Mixin(NewLevelScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
*///?} else {
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
//?}
    //? if <=1.15.2 {
    /*@Shadow
    private String seed;
    *///?} else if <=1.19.3 {
    @Shadow
    @Final
    public MoreOptionsDialog moreOptionsDialog;
     //?} else {
    /*@Shadow
    @Final
    WorldCreator worldCreator;
    *///?}

    protected CreateWorldScreenMixin() {
        super(null);
    }

    @WrapMethod(method = "createLevel")
    private void wrapCreateLevel(Operation<Void> original) {
        EnteredSeedHolder.enteredSeed.set(getSeed());
        try {
            original.call();
        } finally {
            EnteredSeedHolder.enteredSeed.remove();
        }
    }

    @Unique
    private String getSeed() {
        //? if <=1.15.2 {
        /*return this.seed;
         *///?} else if <=1.16.1 {
        return ((MoreOptionsDialogAccessor) this.moreOptionsDialog).getSeedText();
         //?} else if <=1.19.3 {
        /*OptionalLong seed = ((MoreOptionsDialogAccessor) this.moreOptionsDialog).getSeedOpt();
        if (seed.isPresent()) {
            return Long.toString(seed.getAsLong());
        }
        return "";
        *///?} else {
        /*return worldCreator.getSeed();
        *///?}
    }
}
