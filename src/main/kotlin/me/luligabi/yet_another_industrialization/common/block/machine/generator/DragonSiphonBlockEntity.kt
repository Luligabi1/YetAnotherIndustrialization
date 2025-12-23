package me.luligabi.yet_another_industrialization.common.block.machine.generator

import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.machines.multiblocks.SimpleMember
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper
import net.minecraft.resources.ResourceLocation

class DragonSiphonBlockEntity(bep: BEP): YAIGeneratorMultiblockBlockEntity(
    bep,
    YAI.Companion.id(ID),
    arrayOf(SHAPE)
) {

    companion object : YAIMultiblockHelper {

        const val ID = "dragon_egg_energy_siphon"
        const val NAME = "Dragon Egg Energy Siphon"

        private val CASING = SimpleMember.forBlock { YAIBlocks.STEEL_PLATED_END_STONE_BRICKS.get() }
        private val DRAGON_EGG = SimpleMember.forBlockId(ResourceLocation.withDefaultNamespace("dragon_egg"))

        override val pattern = listOf(
            listOf(
                "###",
                "###",
                "###"
            ),
            listOf(
                "#@#",
                "@$@",
                "#@#"
            ),
            listOf(
                "_#_",
                "###",
                "_#_"
            )
        )

        override val materialRules: Map<(Char, Int) -> Boolean, SimpleMember>
            get() = mapOf(
                { char: Char, y: Int -> char == '#' } to CASING,
                { char: Char, y: Int -> char == '@' } to YAIMultiblockHelper.Companion.GLASS_MEMBER,
                { char: Char, y: Int -> char == '$' } to DRAGON_EGG,
            )

        override val hatches: HatchFlags
            get() = HatchFlags.Builder()
                .with(
                    HatchTypes.ITEM_INPUT,
                    HatchTypes.FLUID_INPUT, HatchTypes.FLUID_OUTPUT,
                    HatchTypes.ENERGY_OUTPUT
                )
                .build()

        override val hatchPredicate: Map<(Char, Int) -> Boolean, HatchFlags>
            get() = mapOf(
                { char: Char, _: Int -> char == 'x' } to hatches,
            )

        override val controllerXOffset = -1

        val SHAPE = ShapeTemplate.Builder(YAIMachines.Casings.STEEL_PLATED_END_STONE_BRICKS)
            .addLayer(0)
            .addLayer(1)
            .addLayer(2)
            .build()
    }

    override fun recipeType() = YAIMachines.RecipeTypes.DRAGON_SIPHON
}