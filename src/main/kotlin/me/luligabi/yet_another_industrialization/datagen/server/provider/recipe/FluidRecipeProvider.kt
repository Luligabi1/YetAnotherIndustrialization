package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts

object FluidRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMachineRecipe(
            "centrifuge/nutrient_rich_water",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(Fluids.WATER, 1_000, 1f)
                it.addItemInput(Items.BONE_MEAL, 6, 1f)
                it.addFluidOutput(YAIFluids.NUTRIENT_RICH_WATER.asFluid(), 1_000, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/nutrient_rich_lava",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(Fluids.LAVA, 1_000, 1f)
                it.addItemInput(Items.NETHER_WART, 6, 1f)
                it.addFluidOutput(YAIFluids.NUTRIENT_RICH_LAVA.asFluid(), 1_000, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/dragon_breath",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addItemInput(Items.DRAGON_BREATH, 1, 1f)

                it.addItemOutput(Items.GLASS_BOTTLE, 1, 1f)
                it.addFluidOutput(YAIFluids.DRAGONS_BREATH.asFluid(), 350, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/nutrient_rich_dragon_breath",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(YAIFluids.DRAGONS_BREATH.asFluid(), 1_000, 1f)
                it.addItemInput(Items.POPPED_CHORUS_FRUIT, 6, 1f)
                it.addFluidOutput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/nutrient_rich_dragon_breath_dragon_egg",
            MIMachineRecipeTypes.CENTRIFUGE,
            64, 120*20,
            {
                it.addItemInput(Blocks.DRAGON_EGG, 1, 1f)
                it.addFluidOutput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 16_000, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/impure_dragon_breath_purify",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 7*20,
            {
                it.addFluidInput(YAIFluids.IMPURE_DRAGONS_BREATH.asFluid(), 250, 1f)

                it.addFluidOutput(YAIFluids.DRAGONS_BREATH.asFluid(), 250, 1f)
                it.addItemOutput(MIMaterials.QUARTZ.get(MIMaterialParts.TINY_DUST), 1, 0.15f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/impure_dragon_breath_purify_nutrient",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 12*20,
            {
                it.addFluidInput(YAIFluids.IMPURE_DRAGONS_BREATH.asFluid(), 500, 1f)
                it.addItemInput(Items.POPPED_CHORUS_FRUIT, 2, 1f)

                it.addFluidOutput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 500, 1f)
                it.addItemOutput(MIMaterials.QUARTZ.get(MIMaterialParts.TINY_DUST), 1, 0.4f)
            },
            output
        )
    }

}