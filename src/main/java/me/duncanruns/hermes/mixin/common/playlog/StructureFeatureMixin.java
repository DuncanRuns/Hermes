//? if <=1.12.2 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import me.duncanruns.hermes.playlog.StructureHelper;
import net.minecraft.world.gen.structure.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureFeature.class)
public abstract class StructureFeatureMixin {
    @Shadow
    public abstract String getName();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreateStructure(CallbackInfo ci) {
        String name = getName();
        if (name == null || name.isEmpty()) return;
        StructureHelper.addStructureName(name);
    }
}
*///?}