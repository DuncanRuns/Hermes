package me.duncanruns.hermes.mixin.client.playlog.enteredseed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow
    private String seed;

    //? if <=1.12.2 {
    /*@WrapMethod(method = "buttonClicked")
    private void wrapCreateLevel(ButtonWidget button, Operation<Void> original) {
        if (button.active && button.id == 0) {
            EnteredSeedHolder.enteredSeed.set(getSeed());
            try {
                original.call(button);
            } finally {
                EnteredSeedHolder.enteredSeed.remove();
            }
        } else {
            original.call(button);
        }
    }
    *///?} else {
    @WrapMethod(method = "m_2915360")
    private void wrapCreateLevel(Operation<Void> original) {
        EnteredSeedHolder.enteredSeed.set(getSeed());
        try {
            original.call();
        } finally {
            EnteredSeedHolder.enteredSeed.remove();
        }
    }
    //?}

    @Unique
    private String getSeed() {
        return this.seed;
    }
}
