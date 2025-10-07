package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MI
import aztech.modern_industrialization.MIBlock
import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.MIItem
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseTierCondition
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.DragonSiphonBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.EnergyGenerationCondition
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.ConfigurableMixedStorageMachineBlockEntity
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterialRegistry
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterials
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapedRecipeBuilder
import net.swedz.tesseract.neoforge.compat.vanilla.recipe.ShapelessRecipeBuilder

class RecipeProvider(event: GatherDataEvent): RecipeProvider(event.generator.packOutput, event.lookupProvider) {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        buildItemRecipes(output, lookup)
        buildMachineRecipes(output, lookup)
        buildPartRecipes(output, lookup)
        buildFluidRecipes(output, lookup)

        for (material in YAIMaterials.values()) {
            YAIMaterialRegistry.createRecipesFor(material, output)
        }
    }

    private fun buildItemRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        shapeless(
            "guidebook",
            YAIItems.GUIDEBOOK.get(), 1,
            { it
                .with(MIItem.GUIDE_BOOK.asItem())
                .with(Tags.Items.DYES_MAGENTA)
            },
            output
        )

        shaped(
            "machine_diagnoser",
            YAIItems.MACHINE_DIAGNOSER, 1,
            { it
                .define('B', YAITags.MI_GUIDE_BOOKS)
                .define('G', MIMaterials.BRONZE.get(MIMaterialParts.GEAR).asItem())
                .define('P', MIMaterials.IRON.get(MIMaterialParts.PLATE).asItem())
                .pattern("PPP")
                .pattern("GBG")
                .pattern("PPP")
            },
            output
        )

        addMachineRecipe(
            "assembler/storage_slot_locker",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIItem.CONFIG_CARD, 1, 1f)
                it.addItemInput(Tags.Items.CHESTS, 1, 1f)
                it.addItemInput(Tags.Items.BUCKETS_EMPTY, 1, 1f)
                it.addFluidInput(MIFluids.MOLTEN_REDSTONE, 990, 1f)

                it.addItemOutput(YAIItems.STORAGE_SLOT_LOCKER.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "assembler/machine_remover",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIItem.WRENCH, 1, 1f)
                it.addItemInput(MIItem.ROBOT_ARM, 4, 1f)
                it.addItemInput(MIItem.ELECTRONIC_CIRCUIT, 2, 1f)
                it.addFluidInput(MIFluids.MOLTEN_REDSTONE, 1980, 1f)
                it.addFluidInput(MIFluids.POLYETHYLENE, 500, 1f)

                it.addItemOutput(YAIItems.MACHINE_REMOVER.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "chemical_reactor/cachaca",
            MIMachineRecipeTypes.CHEMICAL_REACTOR,
            6, 7*20,
            {
                it.addItemInput(Items.GLASS_BOTTLE, 3, 1f)
                it.addFluidInput(MIFluids.SUGAR_SOLUTION, 1_000, 1f)
                it.addFluidInput(MIFluids.ETHANOL, 51, 1f)

                it.addItemOutput(YAIItems.CACHACA.get(), 3, 1f)
            },
            output
        )

        addMachineRecipe(
            "chemical_reactor/ai_slop",
            MIMachineRecipeTypes.CHEMICAL_REACTOR,
            256, 7*20,
            {
                it.addItemInput(MIItem.QUANTUM_CIRCUIT, 1, 0.02f)
                it.addFluidInput(Fluids.WATER, 16_000, 1f)

                it.addItemOutput(YAIItems.AI_SLOP.get(), 1, 1f)
            },
            output
        )
    }

    private fun buildMachineRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
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
        buildManualArboreousGreenhouseRecipes(output, lookup)

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
    }

    private fun buildManualArboreousGreenhouseRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addArboreousGreenhouseRecipe(
            "chorus_fruit",
            Items.CHORUS_FRUIT,
            YAIFluids.DRAGONS_BREATH.asFluid(), YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(),
            listOf(
                Triple(Items.CHORUS_FRUIT, 8, 1f),
                Triple(Items.CHORUS_FLOWER, 1, 1f)
            ),
            YAI.id("end_stone"),
            ResourceLocation.withDefaultNamespace("chorus_flower"),
            output
        )
    }

    private fun addArboreousGreenhouseRecipe(
        id: String,
        input: ItemLike,
        fluid: Fluid?, nutrientFluid: Fluid?,
        output: List<Triple<ItemLike, Int, Float>>,
        tier: ResourceLocation,
        model: ResourceLocation,
        recipeOutput: RecipeOutput
    ) {
        if (fluid != null) {
            addMachineRecipe(
                "${ArboreousGreenhouseBlockEntity.ID}/$id",
                YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE,
                15, 60*20,
                {
                    it.addItemInput(input, 1, 0f)
                    it.addFluidInput(fluid, 1_000, 1f)

                    output.forEach { (item, amount, chance) ->
                        it.addItemOutput(item, amount, chance)
                    }
                    it.addItemOutput(input, 1, 0.5f)

                    it.addCondition(ArboreousGreenhouseTierCondition(tier, model))
                },
                recipeOutput
            )
        }

        if (nutrientFluid == null) return

        addMachineRecipe(
            "${ArboreousGreenhouseBlockEntity.ID}/${id}_nutrient",
            YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE,
            15, 60*20,
            {
                it.addItemInput(input, 1, 0f)
                it.addFluidInput(nutrientFluid, 1_000, 1f)

                output.forEach { (item, amount, chance) ->
                    it.addItemOutput(item, amount * 2, (chance * 2).coerceAtMost(1f))
                }
                it.addItemOutput(input, 1, 1f)

                it.addCondition(ArboreousGreenhouseTierCondition(tier, model))
            },
            recipeOutput
        )
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

    private fun buildPartRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addMachineRecipe(
            "assembler/dragon_egg_siphon_catalyst/dragon_breath",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIMaterials.STEEL.get(MIMaterialParts.PLATE), 8, 1f)
                it.addItemInput(MIMaterials.QUARTZ.get(MIMaterialParts.TINY_DUST), 1, 1f)
                it.addFluidInput(YAIFluids.DRAGONS_BREATH.asFluid(), 50, 1f)

                it.addItemOutput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "assembler/dragon_egg_siphon_catalyst/nutrient_rich_dragon_breath",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIMaterials.STEEL.get(MIMaterialParts.PLATE), 8, 1f)
                it.addItemInput(MIMaterials.QUARTZ.get(MIMaterialParts.TINY_DUST), 1, 1f)
                it.addFluidInput(YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 50, 1f)

                it.addItemOutput(YAIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 4, 1f)
            },
            output
        )

        shaped(
            YAIBlocks.SPESB_ID,
            YAIBlocks.STEEL_PLATED_END_STONE_BRICKS.get(), 1,
            { it
                .define('P', MIMaterials.STEEL.get(MIMaterialParts.PLATE).asItem())
                .define('B', Blocks.END_STONE_BRICKS)
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
            },
            output
        )
    }

    private fun buildFluidRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
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


    private fun shapeless(
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
            offerTo(output, YAI.id("craft/$path"))
        }

        if(registerForAssembler) {
            MIMachineRecipeBuilder.fromShapedToAssembler(builder).offerTo(output, YAI.id("assembler/$path"))
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