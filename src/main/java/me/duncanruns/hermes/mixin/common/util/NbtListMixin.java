//? if <=1.7.10 {
/*package me.duncanruns.hermes.mixin.common.util;

import me.duncanruns.hermes.util.NbtToJson;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(NbtList.class)
public abstract class NbtListMixin implements NbtToJson.NbtElementList {
    @Shadow
    private List<NbtElement> elements;

    @Override
    public NbtElement hermes$getElement(int index) {
        return this.elements.get(index);
    }
}
*///?}