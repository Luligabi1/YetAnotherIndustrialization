package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CrafterComponent.class, remap = false)
public interface CrafterComponentAccessor {

    @Accessor("activeRecipe")
    RecipeHolder<MachineRecipe> getActiveRecipe();

    @Accessor("conditionContext")
    MachineProcessCondition.Context getConditionContext();

    @Invoker("takeItemInputs")
    boolean invokeTakeItemInputs(MachineRecipe recipe, boolean simulate);

    @Invoker("putItemOutputs")
    boolean invokePutItemOutputs(MachineRecipe recipe, boolean simulate, boolean toggleLock);

    @Invoker("takeFluidInputs")
    boolean invokeTakeFluidInputs(MachineRecipe recipe, boolean simulate);

    @Invoker("putFluidOutputs")
    boolean invokePutFluidOutputs(MachineRecipe recipe, boolean simulate, boolean toggleLock);

    @Invoker("getRecipes")
    Iterable<RecipeHolder<MachineRecipe>> invokeGetRecipes();

}