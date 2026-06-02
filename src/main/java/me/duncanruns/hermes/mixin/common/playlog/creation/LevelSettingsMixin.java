package me.duncanruns.hermes.mixin.common.playlog.creation;

import me.duncanruns.hermes.playlog.creation.PlayLogCreationSettings;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldSettings.class)
public abstract class LevelSettingsMixin implements PlayLogCreationSettings.Holder {
    @Unique
    private final PlayLogCreationSettings creationSettings = new PlayLogCreationSettings();

    @Override
    public PlayLogCreationSettings hermes$getCreationSettings() {
        return creationSettings;
    }

    @Inject(method = "enableBonusChest", at = @At("HEAD"))
    private void onSetBonusChest(CallbackInfoReturnable<WorldSettings> cir) {
        creationSettings.bonusChest = true;
    }

    // require = 0 because enableCommands is not present on dedicated servers, and making another mixin would be way too dank
    @Inject(method = "enableCommands", at = @At("HEAD"), require = 0)
    private void onEnableCommands(CallbackInfoReturnable<WorldSettings> cir) {
        creationSettings.allowCommands = true;
    }

    //? if <=1.12.2 {
    /*@Inject(method = "setGeneratorOptions", at = @At("HEAD"))
    private void onSetGeneratorOptions(String levelTypeOptions, CallbackInfoReturnable<WorldSettings> cir) {
        creationSettings.levelTypeOptions = new com.google.gson.JsonPrimitive(levelTypeOptions);
    }
    *///?} else {
    @Inject(method = "setGeneratorOptions", at = @At("HEAD"))
    private void onSetGeneratorOptions(com.google.gson.JsonElement levelTypeOptions, CallbackInfoReturnable<WorldSettings> cir) {
        creationSettings.levelTypeOptions = levelTypeOptions;
    }
    //?}
}
