//? if <=1.8.9 {
/*package me.duncanruns.hermes.mixin.common.playlog;

import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.resource.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;
import java.util.Map;

@Mixin(StatusEffect.class)
public interface StatusEffectAccessor {
    @Accessor("REGISTRY")
    static Map<Identifier, StatusEffect> getRegistry() {
        return new HashMap<>(); // gets rid of "might be null" suggestions
    }
}
*///?}
