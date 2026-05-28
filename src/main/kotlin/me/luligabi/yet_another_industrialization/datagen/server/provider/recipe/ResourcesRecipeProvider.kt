package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.material.Fluids
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts

object ResourcesRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addLiquidAirRecipes(output, lookup)
    }

    fun addLiquidAirRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMachineRecipe(
            "scorching_liquid_air_extract",
            MIMachineRecipeTypes.CENTRIFUGE,
            24, 30*20,
            {
                it.addFluidInput(YAIFluids.SCORCHING_LIQUID_AIR, 1_000, 1f)

                it.addItemOutput(MIMaterials.SULFUR.get(MIMaterialParts.TINY_DUST).asItem(), 16, 1f)
                it.addItemOutput(MIMaterials.GOLD.get(MIMaterialParts.TINY_DUST).asItem(), 3, 1f)
                it.addFluidOutput(Fluids.LAVA, 50, 1f)
            },
            output
        )

        addMachineRecipe(
            "gelid_liquid_air_extract",
            MIMachineRecipeTypes.CENTRIFUGE,
            24, 30*20,
            {
                it.addFluidInput(YAIFluids.GELID_LIQUID_AIR, 1_000, 1f)

                it.addItemOutput(MIMaterials.BERYLLIUM.get(MIMaterialParts.TINY_DUST).asItem(), 16, 1f)
                it.addItemOutput(MIMaterials.TUNGSTEN.get(MIMaterialParts.TINY_DUST).asItem(), 3, 1f)
                it.addFluidOutput(MIFluids.ARGON.asFluid(), 14, 1f)
            },
            output
        )
    }

}