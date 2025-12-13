package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.MIItem
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.level.block.Blocks
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts

object PartRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
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

        addMachineRecipe(
            "assembler/singularity_block",
            MIMachineRecipeTypes.ASSEMBLER,
            20, 20*20,
            {
                it.addItemInput(MIItem.SINGULARITY, 9, 1f)
                it.addFluidInput(MIFluids.CRYOFLUID, 250, 1f)
                it.addFluidInput(MIFluids.TRITIUM, 50, 1f)

                it.addItemOutput(YAIBlocks.SINGULARITY_BLOCK.get(), 1, 1f)
            },
            output
        )

        addMachineRecipe(
            "unpacker/singularity_block",
            MIMachineRecipeTypes.UNPACKER,
            20, 20*20,
            {
                it.addItemInput(YAIBlocks.SINGULARITY_BLOCK.get(), 1, 1f)

                it.addItemOutput(MIItem.SINGULARITY, 9, 1f)
            },
            output
        )

        shaped(
            YAIItems.TEMPPROOF_AIR_INTAKE.identifier().location.path,
            YAIItems.TEMPPROOF_AIR_INTAKE, 1,
            { it
                .define('A', MIMaterials.ANNEALED_COPPER.get(MIMaterialParts.PLATE).asItem())
                .define('T', MIMaterials.TITANIUM.get(MIMaterialParts.PLATE).asItem())
                .define('R', MIMaterials.TITANIUM.get(MIMaterialParts.ROTOR).asItem())
                .pattern("ATA")
                .pattern("TRT")
                .pattern("ATA")
            },
            output
        )
    }

}