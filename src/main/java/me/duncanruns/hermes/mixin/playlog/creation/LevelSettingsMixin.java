//? if <=1.15.2 {
/*package me.duncanruns.hermes.mixin.playlog.creation;

import com.google.gson.JsonElement;
import me.duncanruns.hermes.playlog.creation.PlayLogCreationSettings;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelInfo.class)
public abstract class LevelSettingsMixin implements PlayLogCreationSettings.Holder {
    @Unique
    private final PlayLogCreationSettings creationSettings = new PlayLogCreationSettings();

    @Override
    public PlayLogCreationSettings hermes$getCreationSettings() {
        return creationSettings;
    }

    @Inject(method = "setBonusChest", at = @At("HEAD"))
    private void onSetBonusChest(CallbackInfoReturnable<LevelInfo> cir) {
        creationSettings.bonusChest = true;
    }

    // require = 0 because enableCommands is not present on dedicated servers, and making another mixin would be way too dank
    @Inject(method = "enableCommands", at = @At("HEAD"), require = 0)
    private void onEnableCommands(CallbackInfoReturnable<LevelInfo> cir) {
        creationSettings.allowCommands = true;
    }

    @Inject(method = "setGeneratorOptions", at = @At("HEAD"))
    private void onSetGeneratorOptions(JsonElement levelTypeOptions, CallbackInfoReturnable<LevelInfo> cir) {
        creationSettings.levelTypeOptions = levelTypeOptions;
    }
}
*///?}