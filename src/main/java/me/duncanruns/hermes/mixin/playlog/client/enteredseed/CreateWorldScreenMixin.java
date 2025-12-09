package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    //? if >=1.16 {
    @Shadow
    @Final
    public net.minecraft.client.gui.screen.world.MoreOptionsDialog moreOptionsDialog;
    //?} else {
    /*@Shadow private String seed;
     *///?}

    protected CreateWorldScreenMixin(Text title) {
        super(title);
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
        //? if >=1.16 {
        return ((MoreOptionsDialogAccessor) this.moreOptionsDialog).getSeedText();
        //?} else {
        /*return this.seed;
         *///?}
    }
}
