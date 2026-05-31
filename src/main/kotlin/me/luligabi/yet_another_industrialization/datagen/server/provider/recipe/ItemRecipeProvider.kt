package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.MIItem
import aztech.modern_industrialization.MITags
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.common.Tags
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts

object ItemRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        shapeless(
            "guidebook",
            YAIItems.GUIDEBOOK.get(), 1,
            { it
                .with(MIItem.GUIDE_BOOK)
                .with(Tags.Items.DYES_MAGENTA)
            },
            output
        )

        shaped(
            "industrialists_goggles",
            YAIItems.INDUSTRIALISTS_GOGGLES, 1,
            { it
                .define('W', MITags.WRENCHES)
                .define('G', Tags.Items.GLASS_PANES)
                .define('P', MIMaterials.BRONZE.get(MIMaterialParts.PLATE).asItem())
                .pattern(" W ")
                .pattern("GPG")
            },
            output
        )

        shaped(
            "machine_diagnoser",
            YAIItems.MACHINE_DIAGNOSER, 1,
            { it
                .define('B', YAITags.GUIDE_BOOK_OR_BOOK)
                .define('G', MIMaterials.BRONZE.get(MIMaterialParts.GEAR).asItem())
                .define('P', MIMaterials.IRON.get(MIMaterialParts.PLATE).asItem())
                .pattern("PPP")
                .pattern("GBG")
                .pattern("PPP")
            },
            output
        )

        addMachineRecipe(
            "storage_slot_locker",
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
            "machine_remover",
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
            "cachaca",
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
            "ai_slop",
            MIMachineRecipeTypes.CHEMICAL_REACTOR,
            256, 7*20,
            {
                it.addItemInput(MIItem.QUANTUM_CIRCUIT, 1, 0.02f)
                it.addFluidInput(Fluids.WATER, 16_000, 1f)

                it.addItemOutput(YAIItems.AI_SLOP.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "ultradense_metal_ball_burger",
            MIMachineRecipeTypes.PACKER,
            2, 5*20,
            {
                it.addItemInput(Items.BREAD, 1, 1f)
                it.addItemInput(MIItem.ULTRADENSE_METAL_BALL, 1, 1f)
                it.addItemInput(Items.BREAD, 1, 1f)

                it.addItemOutput(YAIItems.ULTRADENSE_METAL_BALL_BURGER.get(), 1, 1f)
            },
            output
        )
    }

}