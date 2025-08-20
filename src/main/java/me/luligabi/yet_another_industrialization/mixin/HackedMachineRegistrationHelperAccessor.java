package me.luligabi.yet_another_industrialization.mixin;

import aztech.modern_industrialization.compat.rei.machines.MachineCategoryParams;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;
import net.swedz.tesseract.neoforge.compat.mi.hack.HackedMachineRegistrationHelper;
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = HackedMachineRegistrationHelper.class, remap = false)
public interface HackedMachineRegistrationHelperAccessor {

    @Invoker("registerReiTiers")
    static void invokeRegisterReiTiers(MIHook hook, String englishName, String machine, MachineRecipeType recipeType, MachineCategoryParams categoryParams, int tiers) {
        throw new AssertionError();
    }
}
