package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(net.minecraft.client.gui.screen.world.CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends net.minecraft.client.gui.screen.Screen {
    @Shadow
    private String seed;

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
        return this.seed;
    }
}
