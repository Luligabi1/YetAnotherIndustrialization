package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.ItemLike
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder

interface YAIRecipeProvider {

    fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider)

    fun shapeless(
        name: String,
        result: ItemLike, resultCount: Int,
        crafting: (ShapelessRecipeBuilder) -> Unit,
        output: RecipeOutput
    ) {
        ShapelessRecipeBuilder().apply {
            crafting.invoke(this)
            output(result, resultCount)
            offerTo(output, YAI.id("craft/$name"))
        }

    }

    fun shaped(
        path: String,
        result: ItemLike, resultCount: Int,
        crafting: (ShapedRecipeBuilder) -> Unit,
        output: RecipeOutput,
        registerForAssembler: Boolean = true
    ) {
        val builder = ShapedRecipeBuilder().apply {
            crafting.invoke(this)
            output(result, resultCount)
            offerTo(output, YAI.id("craft/$path"))
        }

        if(registerForAssembler) {
            MIMachineRecipeBuilder.fromShapedToAssembler(builder).offerTo(output, YAI.id("assembler/$path"))
        }

    }

    fun assembler(
        path: String,
        result: ItemLike, resultCount: Int,
        crafting: (ShapedRecipeBuilder) -> Unit,
        output: RecipeOutput
    ) {
        val builder = ShapedRecipeBuilder().apply {
            crafting.invoke(this)
            output(result, resultCount)
        }

        MIMachineRecipeBuilder.fromShapedToAssembler(builder).offerTo(output, YAI.id("assembler/${path}"))
    }

    fun addMachineRecipe(
        path: String,
        recipeType: MachineRecipeType,
        eu: Int, duration: Int,
        crafting: (MIMachineRecipeBuilder) -> Unit,
        output: RecipeOutput
    ) {
        MIMachineRecipeBuilder(recipeType, eu, duration).apply {
            crafting.invoke(this)
            offerTo(output, YAI.id("${recipeType.id.path}/$path"))
        }
    }

}