package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.components.EnergyComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EnergyComponent.class, remap = false)
public interface EnergyComponentAccessor {

    @Accessor("storedEu")
    long getStoredEu();

}