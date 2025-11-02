package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterialRegistry
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterials
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

class RecipeProvider(event: GatherDataEvent): RecipeProvider(event.generator.packOutput, event.lookupProvider) {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        ItemRecipeProvider.buildRecipes(output, lookup)
        MachineRecipeProvider.buildRecipes(output, lookup)
        ArboreousGreenhouseRecipeProvider.buildRecipes(output, lookup)
        PartRecipeProvider.buildRecipes(output, lookup)
        FluidRecipeProvider.buildRecipes(output, lookup)

        for (material in YAIMaterials.values()) {
            YAIMaterialRegistry.createRecipesFor(material, output)
        }
    }

}