package me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon

import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder

class DragonSiphonRecipeType(id: ResourceLocation): ProxyableMachineRecipeType(id) {

    private lateinit var defaultRecipes: List<RecipeData>

    private fun generate(
        data: RecipeData
    ): RecipeHolder<MachineRecipe> {

        val recipeBuilder = MIMachineRecipeBuilder(
            this,
            1,
            data.duration
        ).apply {
            addItemInput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 1, 1f)
            addFluidInput(data.input, data.inputAmount, 1f)

            addFluidOutput(data.output, data.outputAmount, 1f)
        }

        val recipe = (recipeBuilder.convert() as MachineRecipe).apply {
            conditions = listOf(EnergyGenerationCondition(data.generatedEnergy))
        }

        return RecipeHolder(ResourceLocation.parse("${YAI.ID}:/${DragonSiphonBlockEntity.ID}/${data.id}"), recipe)
    }

    private fun getPredictionRecipes(): MutableList<RecipeHolder<MachineRecipe>> {
        val recipes = mutableListOf<RecipeHolder<MachineRecipe>>()

        if (!::defaultRecipes.isInitialized) {
            defaultRecipes = listOf(
                RecipeData(
                    "dragon_breath", 10*20,
                    YAIFluids.DRAGONS_BREATH.asFluid(), 1_000,
                    YAIFluids.DRAGONS_BREATH.asFluid(), 1_050,
                    10_000
                ),
                RecipeData(
                    "nutrient_rich_dragon_breath", 10*20,
                    YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000,
                    YAIFluids.DRAGONS_BREATH.asFluid(), 2_000,
                    45_000
                )
            )
        }

        defaultRecipes.forEach {
            recipes.add(generate(it))
        }

        return recipes
    }

    override fun fillRecipeList(level: Level, recipeList: MutableList<RecipeHolder<MachineRecipe>>) {
        recipeList.addAll(getManagerRecipes(level))
        if (YAI.CONFIG.monoLootFabricator().runtimeRecipes()) { // FIXME
            recipeList.addAll(getPredictionRecipes())
        }
    }


    private data class RecipeData(
        val id: String, val duration: Int,
        val input: Fluid, val inputAmount: Int,
        val output: Fluid, val outputAmount: Int,
        val generatedEnergy: Long
    )

}