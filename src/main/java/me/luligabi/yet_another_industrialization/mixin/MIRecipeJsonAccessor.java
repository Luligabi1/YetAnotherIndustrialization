package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.recipe.MIRecipeJson;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MIRecipeJson.class, remap = false)
public interface MIRecipeJsonAccessor {

    @Accessor("recipe")
    MachineRecipe getRecipe();

}