package me.luligabi.yet_another_industrialization.common.block.machine

import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.HatchType
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.machines.multiblocks.SimpleMember
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.Tags

interface YAIMultiblockHelper {

    companion object {

        val HATCHES: HatchFlags
            get() = HatchFlags.Builder()
                .with(
                    HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT,
                    HatchType.FLUID_INPUT, HatchType.FLUID_OUTPUT,
                    HatchType.ENERGY_INPUT
                )
                .build()

        val GLASS_MEMBER = object : SimpleMember {

            override fun matchesState(state: BlockState) = state.`is`(Tags.Blocks.GLASS_BLOCKS)

            override fun getPreviewState() = Blocks.GLASS.defaultBlockState()
        }

    }

    val pattern: List<List<String>>

    val materialRules: Map<(Char, Int) -> Boolean, SimpleMember>

    val hatchPredicate: (SimpleMember) -> Boolean
        get() = { true }

    val hatches: HatchFlags
        get() = HATCHES

    val controllerXOffset: Int
        get() = 0

    fun ShapeTemplate.Builder.addLayer(y: Int, patternIndex: Int = y): ShapeTemplate.Builder {

        for (z in pattern[patternIndex].indices) {
            val row = pattern[patternIndex][z]
            for (x in row.indices) {
                if (row[x] == '_') continue

                val block = materialRules.entries.find { it.key(row[x], y) }?.value ?: continue
                add(x + controllerXOffset, y, z, block, if (hatchPredicate(block)) hatches else null)
            }
        }

        return this
    }

}