package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.inventory.MIStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = MIStorage.class, remap = false)
public interface MIStorageAccessor {

    @Accessor("stacks")
    List<?> getStacks();
}
