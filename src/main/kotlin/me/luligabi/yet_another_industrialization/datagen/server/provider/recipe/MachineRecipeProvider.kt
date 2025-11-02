package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MI
import aztech.modern_industrialization.MIBlock
import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.MIItem
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.DragonSiphonBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.EnergyGenerationCondition
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.ConfigurableMixedStorageMachineBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch.FluidTrashCanHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch.ItemTrashCanHatch
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterials
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder

object MachineRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        shaped(
            ArboreousGreenhouseBlockEntity.ID,
            YAIMachines.getMachineFromId(ArboreousGreenhouseBlockEntity.ID), 1,
            { it
                .define('H', MIBlock.BASIC_MACHINE_HULL)
                .define('C', MIItem.ANALOG_CIRCUIT)
                .define('P', MIItem.PUMP)
                .define('D', ItemTags.DIRT)
                .define('I', MIMaterials.INVAR.get(MIMaterialParts.MACHINE_CASING_SPECIAL).asBlock())
                .pattern("CIC")
                .pattern("PHP")
                .pattern("CDC")
            },
            output
        )

        shaped(
            YAIMachines.CP_ID,
            YAIMachines.getMachineFromId(YAIMachines.CP_ID), 1,
            { it
                .define('P', MIMaterials.ALUMINUM.get(MIMaterialParts.LARGE_PLATE).asItem())
                .define('C', MIItem.ELECTRONIC_CIRCUIT)
                .define('T', MIMaterials.TIN.get(MIMaterialParts.CABLE).asItem())
                .define('H', MIBlock.BASIC_MACHINE_HULL)
                .pattern("PCP")
                .pattern("THT")
                .pattern("PCP")
            },
            output
        )
        buildCryogenicPrecipitatorRecipes(output, lookup)

        shaped(
            DragonSiphonBlockEntity.ID,
            YAIMachines.getMachineFromId(DragonSiphonBlockEntity.ID), 1,
            { it
                .define('P', MIItem.LARGE_PUMP)
                .define('D', Items.DRAGON_BREATH)
                .define('G', MIMaterials.STEEL.get(MIMaterialParts.GEAR).asItem())
                .define('H', MIBlock.ADVANCED_MACHINE_HULL)
                .pattern("PDP")
                .pattern("GHG")
                .pattern("PDP")
            },
            output
        )
        buildDragonSiphonRecipes(output, lookup)

        /** Large Storage Unit */
        shaped(
            LargeStorageUnitBlockEntity.ID,
            YAIMachines.getMachineFromId(LargeStorageUnitBlockEntity.ID), 1,
            { it
                .define('M', YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.MACHINE_CASING_SPECIAL).asBlock())
                .define('C', MIItem.ELECTRONIC_CIRCUIT)
                .define('B', MIMaterials.REDSTONE.get(MIMaterialParts.BATTERY).asItem())
                .define('P', MIItem.PORTABLE_STORAGE_UNIT)
                .pattern("PCP")
                .pattern("BMB")
                .pattern("PBP")
            },
            output
        )
        shaped(
            LargeStorageUnitHatch.ID_INPUT,
            YAIMachines.getMachineFromId(LargeStorageUnitHatch.ID_INPUT), 1,
            { it
                .define('M', YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.MACHINE_CASING_SPECIAL).asBlock())
                .define('R', MIMaterials.REDSTONE.get(MIMaterialParts.BATTERY).asItem())
                .pattern("R")
                .pattern("M")
            },
            output
        )
        shaped(
            LargeStorageUnitHatch.ID_OUTPUT,
            YAIMachines.getMachineFromId(LargeStorageUnitHatch.ID_OUTPUT), 1,
            { it
                .define('M', YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.MACHINE_CASING_SPECIAL).asBlock())
                .define('R', MIMaterials.REDSTONE.get(MIMaterialParts.BATTERY).asItem())
                .pattern("M")
                .pattern("R")
            },
            output
        )
        shapeless(
            "${LargeStorageUnitHatch.ID_INPUT}_convert",
            YAIMachines.getMachineFromId(LargeStorageUnitHatch.ID_INPUT), 1,
            { it.with(YAIMachines.getMachineFromId(LargeStorageUnitHatch.ID_OUTPUT)) },
            output
        )
        shapeless(
            "${LargeStorageUnitHatch.ID_OUTPUT}_convert",
            YAIMachines.getMachineFromId(LargeStorageUnitHatch.ID_OUTPUT), 1,
            { it.with(YAIMachines.getMachineFromId(LargeStorageUnitHatch.ID_INPUT)) },
            output
        )
        /***/

        /** Configurable Mixed Storage */
        shaped(
            ConfigurableMixedStorageMachineBlockEntity.ID,
            YAIMachines.getMachineFromId(ConfigurableMixedStorageMachineBlockEntity.ID), 4,
            { it
                .define('C', MIItem.ELECTRONIC_CIRCUIT)
                .define('H', Items.HOPPER)
                .define('T', MIMaterials.STAINLESS_STEEL.get(MIMaterialParts.TANK).asBlock())
                .define('S', MIMaterials.STAINLESS_STEEL.get(MIMaterialParts.CLEAN_MACHINE_CASING).asBlock())

                .pattern("CTC")
                .pattern("HSH")
                .pattern("CTC")
            },
            output
        )

        shaped(
            "${ConfigurableMixedStorageMachineBlockEntity.ID}_alt",
            YAIMachines.getMachineFromId(ConfigurableMixedStorageMachineBlockEntity.ID), 4,
            { it
                .define('C', MIItem.ELECTRONIC_CIRCUIT)
                .define('H', Items.HOPPER)
                .define('T', MIMaterials.STAINLESS_STEEL.get(MIMaterialParts.TANK).asBlock())
                .define('S', MIMaterials.STAINLESS_STEEL.get(MIMaterialParts.CLEAN_MACHINE_CASING).asBlock())

                .pattern("CHC")
                .pattern("TST")
                .pattern("CHC")
            },
            output,
            false
        )

        shapeless(
            "${ConfigurableMixedStorageMachineBlockEntity.ID}_upgrade",
            YAIMachines.getMachineFromId(ConfigurableMixedStorageMachineBlockEntity.ID), 2,
            { it
                .with(MI.id("configurable_chest"))
                .with(MI.id("configurable_tank"))
                .with(MIMaterials.STAINLESS_STEEL.get(MIMaterialParts.CLEAN_MACHINE_CASING).asBlock())
                .with(MIItem.ELECTRONIC_CIRCUIT)
            },
            output
        )

        assembler(
            "${ConfigurableMixedStorageMachineBlockEntity.ID}_upgrade",
            YAIMachines.getMachineFromId(ConfigurableMixedStorageMachineBlockEntity.ID), 2,
            { it
                .define('S', MIMaterials.STAINLESS_STEEL.get(MIMaterialParts.CLEAN_MACHINE_CASING).asBlock())
                .define('E', MIItem.ELECTRONIC_CIRCUIT)
                .define('C', MI.id("configurable_chest"))
                .define('T', MI.id("configurable_tank"))
                .pattern("SEC")
                .pattern("T")
            },
            output
        )
        /***/

        /** Hatches */
        buildHatchRecipes(output, lookup)
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

        addMachineRecipe(
            "${YAIMachines.CP_ID}/powder_snow_bucket_nutrient",
            YAIMachines.RecipeTypes.CRYOGENIC_PRECIPITATOR,
            8, 30,
            {
                it.addItemInput(Items.BUCKET, 2, 1f)
                it.addFluidInput(YAIFluids.NUTRIENT_RICH_WATER, 1_000, 1f)
                it.addFluidInput(MIFluids.CRYOFLUID, 4, 1f)

                it.addItemOutput(Items.POWDER_SNOW_BUCKET, 1, 1f)
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

    private fun buildDragonSiphonRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMachineRecipe(
            "${DragonSiphonBlockEntity.ID}/dragon_breath",
            YAIMachines.RecipeTypes.DRAGON_SIPHON,
            1, 8*20,
            {
                it.addItemInput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 1, 1f)

                it.addFluidInput(YAIFluids.DRAGONS_BREATH.asFluid(), 1_000, 1f)
                it.addFluidOutput(YAIFluids.IMPURE_DRAGONS_BREATH.asFluid(), 1_250, 1f)

                it.addCondition(EnergyGenerationCondition(10_000))
            },
            output
        )

        addMachineRecipe(
            "${DragonSiphonBlockEntity.ID}/nutrient_dragon_breath",
            YAIMachines.RecipeTypes.DRAGON_SIPHON,
            1, 20*20,
            {
                it.addItemInput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 1, 1f)

                it.addFluidInput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000, 1f)
                it.addFluidOutput(YAIFluids.IMPURE_DRAGONS_BREATH.asFluid(), 2_500, 1f)

                it.addCondition(EnergyGenerationCondition(45_000))
            },
            output
        )
    }

    private fun buildHatchRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMixedHatchRecipes("bronze", output, lookup)
        addMixedHatchRecipes("steel", output, lookup)
        addMixedHatchRecipes("advanced", output, lookup)
        addMixedHatchRecipes("turbo", output, lookup)
        addMixedHatchRecipes("highly_advanced", output, lookup)

        addTrashcanHatchRecipes(output, lookup)
    }

    private fun addMixedHatchRecipes(
        tier: String,
        output: RecipeOutput, lookup: HolderLookup.Provider
    ) {
        fun offerRecipes(itemHatch: ResourceLocation, fluidHatch: ResourceLocation, result: String) {
            val shapeless = ShapelessRecipeBuilder().apply {
                with(lookup.lookup(Registries.ITEM).get()
                    .get(ResourceKey.create(Registries.ITEM, itemHatch)).get().value())
                with(lookup.lookup(Registries.ITEM).get()
                    .get(ResourceKey.create(Registries.ITEM, fluidHatch)).get().value())
                output(lookup.lookup(Registries.ITEM).get()
                    .get(ResourceKey.create(Registries.ITEM, YAI.id(result))).get().value(), 1)
            }
            shapeless.offerTo(output, YAI.id("craft/$result"))
            MIMachineRecipeBuilder.fromShapelessToPacker(shapeless).offerTo(output, YAI.id("packer/$result"))
            MIMachineRecipeBuilder.fromShapelessToUnpackerAndFlip(shapeless).offerTo(output, YAI.id("unpacker/$result"))
        }

        val itemInHatch = ResourceLocation.parse("${MI.ID}:${tier}_item_input_hatch")
        val fluidInHatch = ResourceLocation.parse("${MI.ID}:${tier}_fluid_input_hatch")
        val itemOutHatch = ResourceLocation.parse("${MI.ID}:${tier}_item_output_hatch")
        val fluidOutHatch = ResourceLocation.parse("${MI.ID}:${tier}_fluid_output_hatch")

        val mixedInHatch = "${tier}_mixed_input_hatch"
        val mixedOutHatch = "${tier}_mixed_output_hatch"

        offerRecipes(itemInHatch, fluidInHatch, mixedInHatch)
        offerRecipes(itemOutHatch, fluidOutHatch, mixedOutHatch)
    }

    private fun addTrashcanHatchRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        fun offerRecipes(hatch: ResourceLocation, result: String) {
            val shapeless = ShapelessRecipeBuilder().apply {
                with(lookup.lookup(Registries.ITEM).get()
                    .get(ResourceKey.create(Registries.ITEM, hatch)).get().value())
                with(MIBlock.TRASH_CAN)
                output(lookup.lookup(Registries.ITEM).get()
                    .get(ResourceKey.create(Registries.ITEM, YAI.id(result))).get().value(), 1)
            }
            shapeless.offerTo(output, YAI.id("craft/$result"))
            MIMachineRecipeBuilder.fromShapelessToPacker(shapeless).offerTo(output, YAI.id("packer/$result"))
            MIMachineRecipeBuilder.fromShapelessToUnpackerAndFlip(shapeless).offerTo(output, YAI.id("unpacker/$result"))
        }

        val itemOutHatch = ResourceLocation.parse("${MI.ID}:steel_item_output_hatch")
        val fluidOutHatch = ResourceLocation.parse("${MI.ID}:steel_fluid_output_hatch")

        offerRecipes(itemOutHatch, ItemTrashCanHatch.ID)
        offerRecipes(fluidOutHatch, FluidTrashCanHatch.ID)
    }


}