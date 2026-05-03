package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow
    @Final
    private WorldCreationUiState uiState;

    @SuppressWarnings("all")
    protected CreateWorldScreenMixin() {
        super(null);
    }

    @WrapMethod(method = "onCreate")
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
        return uiState.getSeed();
    }
}
