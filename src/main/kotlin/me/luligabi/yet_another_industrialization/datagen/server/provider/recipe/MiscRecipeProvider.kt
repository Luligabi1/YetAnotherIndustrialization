package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items

object MiscRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMiscDragonsBreathUsages(output, lookup)
    }

    fun addMiscDragonsBreathUsages(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMachineRecipe(
            "elytra_duplication",
            MIMachineRecipeTypes.MIXER,
            2, 5*20,
            {
                it.addItemInput(Items.ELYTRA, 1, 1f)
                it.addItemInput(Items.PHANTOM_MEMBRANE, 4, 1f)
                it.addFluidInput(YAIFluids.DRAGONS_BREATH, 2_000, 1f)

                it.addItemOutput(Items.ELYTRA, 2, 1f)
            },
            output
        )

    }

}