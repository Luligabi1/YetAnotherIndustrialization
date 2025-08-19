package me.luligabi.hostile_neural_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import aztech.modern_industrialization.machines.recipe.MachineRecipe
import me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items


object ArboreousGreenhouseRecipeProvider {

    class BonsaiGenData private constructor(builder: Builder) {

        val mod: String = builder.mod
        val model = builder.model
        val soilType = builder.soilType
        val sapling = builder.sapling
        val output = builder.output


        class Builder {
            lateinit var mod: String
                private set
            lateinit var model: ResourceLocation
                private set
            lateinit var soilType: ResourceLocation
                private set
            lateinit var sapling: ResourceLocation
                private set
            lateinit var output: List<MachineRecipe.ItemOutput>
                private set
        }


    }

    fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
    }

    private fun addRecipe(output: RecipeOutput, treeId: ResourceLocation) {
        HNIRecipeProvider.addMachineRecipe(
            "${ArboreousGreenhouseBlockEntity.ID}/${treeId.namespace}/${treeId.path}",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(HNIFluids.DRAGONS_BREATH.asFluid(), 1_000, 1f)
                it.addItemInput(Items.POPPED_CHORUS_FRUIT, 1, 1f)
                it.addFluidOutput(HNIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000, 1f)
            },
            output
        )

        HNIRecipeProvider.addMachineRecipe(
            "${ArboreousGreenhouseBlockEntity.ID}/${treeId.namespace}/${treeId.path}_nutrient",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(HNIFluids.DRAGONS_BREATH.asFluid(), 1_000, 1f)
                it.addItemInput(Items.POPPED_CHORUS_FRUIT, 1, 1f)
                it.addFluidOutput(HNIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000, 1f)
            },
            output
        )
    }




}