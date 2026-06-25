//? if <=1.12.2 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.hermes.playlog.StructureHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.structure.StructureBox;
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

    @WrapOperation(
            method = "findStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/StructureBox;contains(Lnet/minecraft/util/math/Vec3i;)Z",
                    ordinal = 0
            )
    )
    private boolean lenientSearch(StructureBox instance, Vec3i vec, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        if (StructureHelper.LENIENT_SEARCH.get()) {
            // match only x and z
            return original.call(instance, new BlockPos(pos.getX(), instance.minY, pos.getZ()));
        } else {
            return original.call(instance, pos);
        }
    }
}
*///?}