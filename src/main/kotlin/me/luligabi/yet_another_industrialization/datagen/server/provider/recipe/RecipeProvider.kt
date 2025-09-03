package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.MIItem
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import dev.shadowsoffire.hostilenetworks.Hostile
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder


class RecipeProvider(event: GatherDataEvent): RecipeProvider(event.generator.packOutput, event.lookupProvider) {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {

        shapeless(
            "guidebook",
            YAIItems.GUIDEBOOK.get(), 1,
            { builder -> builder
                .with(MIItem.GUIDE_BOOK.asItem())
                .with(Hostile.Items.BLANK_DATA_MODEL.value())
            },
            output
        )








//        addMachineRecipe(
//            "mixer/nutrient_rich_water",
//            MIMachineRecipeTypes.MIXER,
//            10, 10,
//            {
//                it.addItemInput(HNIFluids., 1, 1f)
//                it.addFluidOutput(HNIFluids.DRAGONS_BREATH.asFluid(), 250, 1f)
//            },
//            output
//        )

        /** ITEMS */
        addMachineRecipe(
            "assembler/machine_remover",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIItem.WRENCH, 1, 1f)
                it.addItemInput(MIItem.PISTON, 2, 1f)
                it.addFluidInput(MIFluids.MOLTEN_REDSTONE, 500, 1f)

                it.addItemOutput(YAIItems.MACHINE_REMOVER.get(), 1, 1f)
            },
            output
        )

        /** PARTS */
    private fun buildMachineRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        // TODO cryogenic precipitator
        buildCryogenicPrecipitatorRecipes(output, lookup)
    }
    private fun buildCryogenicPrecipitatorRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addCryogenicPrecipitatorRecipe(
            "snowball",
            Fluids.WATER, 400,
            2,
            Items.SNOWBALL, 4,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "snow_block",
            Fluids.WATER, 1_000,
            8,
            Blocks.SNOW_BLOCK, 4,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "snow",
            Fluids.WATER, 1_000,
            2,
            Blocks.SNOW, 6,
            output
        )

        addMachineRecipe(
            "${YAIMachines.CP_ID}/powder_snow_bucket",
            YAIMachines.RecipeTypes.CRYOGENIC_PRECIPITATOR,
            8, 30,
            {
                it.addItemInput(Items.BUCKET, 1, 1f)
                it.addFluidInput(Fluids.WATER, 1_000, 1f)
                it.addFluidInput(MIFluids.CRYOFLUID, 4, 1f)

                it.addItemOutput(Items.POWDER_SNOW_BUCKET, 1, 1f)
                it.addFluidOutput(MIFluids.ARGON.asFluid(), 2, 1f)
            },
            output
        )

        addCryogenicPrecipitatorRecipe(
            "ice",
            Fluids.WATER, 1_000,
            1,
            Blocks.ICE, 1,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "packed_ice",
            Fluids.WATER, 4_000,
            8,
            Blocks.PACKED_ICE, 1,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "blue_ice",
            Fluids.WATER, 4_000,
            64,
            Blocks.BLUE_ICE, 1,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "obsidian",
            Fluids.LAVA, 1_000,
            2,
            Blocks.OBSIDIAN, 4,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "crying_obsidian",
            Fluids.LAVA, 1_000,
            2,
            Blocks.CRYING_OBSIDIAN, 4,
            output
        )

        addCryogenicPrecipitatorRecipe(
            "basalt",
            Fluids.LAVA, 500,
            4,
            Blocks.BASALT, 8,
            output
        )
    }

    private fun addCryogenicPrecipitatorRecipe(
        id: String,
        fluid: Fluid, fluidAmount: Int,
        cryofluidAmount: Int,
        output: ItemLike, outputAmount: Int,
        recipeOutput: RecipeOutput
    ) {

        val argonAmount = (cryofluidAmount * 0.65).toInt()
        val heliumAmount = (cryofluidAmount * 0.25).toInt()

        addMachineRecipe(
            "${YAIMachines.CP_ID}/$id",
            YAIMachines.RecipeTypes.CRYOGENIC_PRECIPITATOR,
            8, 30,
            {
                it.addFluidInput(fluid, fluidAmount, 1f)
                it.addFluidInput(MIFluids.CRYOFLUID, cryofluidAmount, 1f)

                it.addItemOutput(output, outputAmount, 1f)

                if (argonAmount > 0) {
                    it.addFluidOutput(MIFluids.ARGON.asFluid(), argonAmount, 1f)

                    if (heliumAmount > 0) {
                        it.addFluidOutput(MIFluids.HELIUM.asFluid(), heliumAmount, 1f)
                    }
                }

            },
            recipeOutput
        )

        val nutrientFluid = when (fluid) {
            Fluids.WATER -> YAIFluids.NUTRIENT_RICH_WATER.asFluid()
            Fluids.LAVA -> YAIFluids.NUTRIENT_RICH_LAVA.asFluid()
            else -> return
        }

        addMachineRecipe(
            "${YAIMachines.CP_ID}/${id}_nutrient",
            YAIMachines.RecipeTypes.CRYOGENIC_PRECIPITATOR,
            8, 30,
            {
                it.addFluidInput(nutrientFluid, fluidAmount, 1f)
                it.addFluidInput(MIFluids.CRYOFLUID, cryofluidAmount, 1f)

                it.addItemOutput(output, outputAmount * 2, 1f)

                if (argonAmount > 0) {
                    it.addFluidOutput(MIFluids.ARGON.asFluid(), argonAmount, 1f)

                    if (heliumAmount > 0) {
                        it.addFluidOutput(MIFluids.HELIUM.asFluid(), argonAmount, 1f)
                    }
                }
            },
            recipeOutput
        )
    }
        addMachineRecipe(
            "assembler/dragon_egg_siphon_catalyst/dragon_breath",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIMaterials.TITANIUM.getPart(MIParts.PLATE), 1, 1f)
                it.addFluidInput(YAIFluids.DRAGONS_BREATH.asFluid(), 5, 1f)

                it.addItemOutput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "assembler/dragon_egg_siphon_catalyst/nutrient_rich_dragon_breath",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIMaterials.TITANIUM.getPart(MIParts.PLATE), 1, 1f)
                it.addFluidInput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 5, 1f)

                it.addItemOutput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 4, 1f)
            },
            output
        )


        /** FLUIDS */
        addMachineRecipe(
            "centrifuge/nutrient_rich_water",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(Fluids.WATER, 1_000, 1f)
                it.addItemInput(Items.BONE_MEAL, 1, 1f)
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
                it.addItemInput(Items.NETHER_WART, 1, 1f)
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
                it.addItemInput(Items.POPPED_CHORUS_FRUIT, 1, 1f)
                it.addFluidOutput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000, 1f)
            },
            output
        )

        buildCompatRecipes(output, lookup)
    }

    private fun buildCompatRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {

    }


    private fun shapeless(
        path: String,
        result: ItemLike, resultCount: Int,
        crafting: (ShapelessRecipeBuilder) -> Unit,
        output: RecipeOutput
    ) {

        ShapelessRecipeBuilder().apply {
            crafting.invoke(this)
            output(result, resultCount)
            offerTo(output, YAI.id(path))
        }

    }

    private fun shaped(
        path: String,
        result: ItemLike, resultCount: Int,
        crafting: (ShapedRecipeBuilder) -> Unit,
        output: RecipeOutput,
        registerForAssembler: Boolean = true
    ) {

        val builder = ShapedRecipeBuilder().apply {
            crafting.invoke(this)
            output(result, resultCount)
            offerTo(output, YAI.id(path))
        }

        if(registerForAssembler) {
            MIMachineRecipeBuilder.fromShapedToAssembler(builder).offerTo(output, YAI.id("${path}_assembler"))
        }

    }

    private fun assembler(
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

    companion object {

        fun addMachineRecipe(
            path: String,
            recipeType: MachineRecipeType,
            eu: Int, duration: Int,
            crafting: (MIMachineRecipeBuilder) -> Unit,
            output: RecipeOutput
        ) {
            MIMachineRecipeBuilder(recipeType, eu, duration).apply {
                crafting.invoke(this)
                offerTo(output, YAI.id(path))
            }
        }

    }

}