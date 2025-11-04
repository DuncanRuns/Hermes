package me.duncanruns.hermes.mixin.playlog.client.enteredseed;

import me.duncanruns.hermes.playlog.enteredseed.EnteredSeedHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow
    private String seed;

    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void storeEnteredSeed(CallbackInfo ci) {
        EnteredSeedHolder.enteredSeed.set(seed);
    }
}
