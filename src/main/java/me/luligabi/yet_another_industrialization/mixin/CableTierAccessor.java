package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.api.energy.CableTier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = CableTier.class, remap = false)
public interface CableTierAccessor {

    @Accessor("tiers")
    static Map<String, CableTier> getTiers() {
        throw new AssertionError();
    }

}