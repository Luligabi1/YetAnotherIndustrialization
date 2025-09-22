package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractCraftingMultiblockBlockEntity.class, remap = false)
public interface AbstractCraftingMultiblockBlockEntityAccessor {

    @Accessor("isActive")
    IsActiveComponent getIsActive();
}
