package me.luligabi.hostile_neural_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MIBlock
import aztech.modern_industrialization.MIItem
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import dev.shadowsoffire.hostilenetworks.Hostile
import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.block.HNIBlocks
import me.luligabi.hostile_neural_industrialization.common.block.machine.HNIMachines
import me.luligabi.hostile_neural_industrialization.common.block.machine.loot_fabricator.large.LargeLootFabricatorBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.loot_fabricator.mono.MonoLootFabricatorBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.sim_chamber.electric.ElectricSimChamberBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.sim_chamber.large.LargeSimChamberBlockEntity
import me.luligabi.hostile_neural_industrialization.common.item.HNIItems
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
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


class HNIRecipeProvider(event: GatherDataEvent): RecipeProvider(event.generator.packOutput, event.lookupProvider) {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {

        shapeless(
            "guidebook",
            HNIItems.GUIDEBOOK.get(), 1,
            { builder -> builder
                .with(MIItem.GUIDE_BOOK.asItem())
                .with(Hostile.Items.BLANK_DATA_MODEL.value())
            },
            output
        )

        shaped(
            "prediction_machine_casing",
            HNIBlocks.PREDICTION_MACHINE_CASING.get(), 1,
            { builder -> builder
                .define('P', Hostile.Items.PREDICTION_MATRIX.value())
                .define('S', MIMaterials.STAINLESS_STEEL.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock())
                .pattern("PPP")
                .pattern("PSP")
                .pattern("PPP")
            },
            output
        )


        shaped(
            "machine/electric_simulation_chamber",
            HNIMachines.getMachineFromId(ElectricSimChamberBlockEntity.ID), 1,
            { builder -> builder
                .define('C', MIItem.CAPACITOR)
                .define('A', MIItem.ANALOG_CIRCUIT)
                .define('R', MIItem.RESISTOR)
                .define('S', Hostile.Blocks.SIM_CHAMBER.value())
                .define('H', MIBlock.BASIC_MACHINE_HULL)
                .pattern("CAC")
                .pattern("RSR")
                .pattern("CHC")
            },
            output
        )

        shaped(
            "machine/mono_loot_fabricator",
            HNIMachines.getMachineFromId(MonoLootFabricatorBlockEntity.ID), 1,
            { builder -> builder
                .define('C', MIItem.CAPACITOR)
                .define('A', MIItem.ANALOG_CIRCUIT)
                .define('R', MIItem.RESISTOR)
                .define('F', Hostile.Blocks.LOOT_FABRICATOR.value())
                .define('H', MIBlock.BASIC_MACHINE_HULL)
                .pattern("CAC")
                .pattern("RFR")
                .pattern("CHC")
            },
            output
        )

        shaped(
            "machine/large_simulation_chamber",
            HNIMachines.getMachineFromId(LargeSimChamberBlockEntity.ID), 1,
            { builder -> builder
                .define('P', HNIBlocks.PREDICTION_MACHINE_CASING.get())
                .define('S', MIMaterials.STAINLESS_STEEL.getPart(MIParts.LARGE_PLATE))
                .define('F', Hostile.Blocks.SIM_CHAMBER.value())
                .define('M', MIItem.ADVANCED_MOTOR)
                .pattern("PSP")
                .pattern("MFM")
                .pattern("PSP")
            },
            output
        )

        shaped(
            "machine/large_loot_fabricator",
            HNIMachines.getMachineFromId(LargeLootFabricatorBlockEntity.ID), 1,
            { builder -> builder
                .define('P', HNIBlocks.PREDICTION_MACHINE_CASING.get())
                .define('S', MIMaterials.STAINLESS_STEEL.getPart(MIParts.LARGE_PLATE))
                .define('F', Hostile.Blocks.LOOT_FABRICATOR.value())
                .define('M', MIItem.ADVANCED_MOTOR)
                .pattern("PSP")
                .pattern("MFM")
                .pattern("PSP")
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

        /** PARTS */
        addMachineRecipe(
            "assembler/dragon_egg_siphon_catalyst/dragon_breath",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIMaterials.TITANIUM.getPart(MIParts.PLATE), 1, 1f)
                it.addFluidInput(HNIFluids.DRAGONS_BREATH.asFluid(), 5, 1f)

                it.addItemOutput(HNIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "assembler/dragon_egg_siphon_catalyst/nutrient_rich_dragon_breath",
            MIMachineRecipeTypes.ASSEMBLER,
            8, 10*20,
            {
                it.addItemInput(MIMaterials.TITANIUM.getPart(MIParts.PLATE), 1, 1f)
                it.addFluidInput(HNIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 5, 1f)

                it.addItemOutput(HNIItems.DRAGON_EGG_SIPHON_CATALYST.get(), 4, 1f)
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
                it.addFluidOutput(HNIFluids.NUTRIENT_RICH_WATER.asFluid(), 1_000, 1f)
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
                it.addFluidOutput(HNIFluids.NUTRIENT_RICH_LAVA.asFluid(), 1_000, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/dragon_breath",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addItemInput(Items.DRAGON_BREATH, 1, 1f)
                it.addFluidOutput(HNIFluids.DRAGONS_BREATH.asFluid(), 350, 1f)
            },
            output
        )

        addMachineRecipe(
            "centrifuge/nutrient_rich_dragon_breath",
            MIMachineRecipeTypes.CENTRIFUGE,
            8, 10*20,
            {
                it.addFluidInput(HNIFluids.DRAGONS_BREATH.asFluid(), 1_000, 1f)
                it.addItemInput(Items.POPPED_CHORUS_FRUIT, 1, 1f)
                it.addFluidOutput(HNIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(), 1_000, 1f)
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
            offerTo(output, HNI.id(path))
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
            offerTo(output, HNI.id(path))
        }

        if(registerForAssembler) {
            MIMachineRecipeBuilder.fromShapedToAssembler(builder).offerTo(output, HNI.id("${path}_assembler"))
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

        MIMachineRecipeBuilder.fromShapedToAssembler(builder).offerTo(output, HNI.id("assembler/${path}"))
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
                offerTo(output, HNI.id(path))
            }
        }

    }

}