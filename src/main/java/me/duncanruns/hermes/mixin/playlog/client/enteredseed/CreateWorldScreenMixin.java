package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

//? if <=1.14 {
/*@Mixin(net.minecraft.client.gui.menu.NewLevelScreen.class)
public abstract class CreateWorldScreenMixin extends net.minecraft.client.gui.Screen {
*///?} else {
@Mixin(net.minecraft.client.gui.screen.world.CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends net.minecraft.client.gui.screen.Screen {
//?}
    //? if <=1.15.2 {
    /*@Shadow
    private String seed;
    *///?} else if <=1.19.3 {
    @Shadow
    @org.spongepowered.asm.mixin.Final
    public net.minecraft.client.gui.screen.world.MoreOptionsDialog moreOptionsDialog;
     //?} else {
    /*@Shadow
    @org.spongepowered.asm.mixin.Final
    net.minecraft.client.gui.screen.world.WorldCreator worldCreator;
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
        /*java.util.OptionalLong seed = ((MoreOptionsDialogAccessor) this.moreOptionsDialog).getSeedOpt();
        if (seed.isPresent()) {
            return Long.toString(seed.getAsLong());
        }
        return "";
        *///?} else {
        /*return worldCreator.getSeed();
        *///?}
    }
}
