package me.luligabi.yet_another_industrialization.common.block.machine.colorizer

import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.machines.multiblocks.SimpleMember
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import aztech.modern_industrialization.util.Tickable
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper
import me.luligabi.yet_another_industrialization.common.block.machine.util.flexible_recipe.ElectricFlexibleRecipeMultiblockBlockEntity

class ColorizerBlockEntity(
    bep: BEP
) : ElectricFlexibleRecipeMultiblockBlockEntity(
    bep,
    MachineGuiParameters.Builder(YAI.id(ID), false).backgroundHeight(153).build(),
    OrientationComponent.Params(false, false, false),
    arrayOf(SHAPE)
), Tickable, MultiblockInventoryComponentHolder {

    companion object : YAIMultiblockHelper {

        const val ID = "colorizer"
        const val NAME = "Colorizer"

        private val STEEL_CASING = SimpleMember.forBlock { MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING).asBlock() }
        private val STEEL_CASING_PIPE = SimpleMember.forBlock { MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING_PIPE).asBlock() }

        override val pattern = listOf(
            listOf(
                "###",
                "@@@",
                "###"
            ),
            listOf(
                "###",
                "@_@",
                "###"
            ),
            listOf(
                "###",
                "@@@",
                "###"
            )
        )

        override val materialRules: Map<(Char, Int) -> Boolean, SimpleMember>
            get() = mapOf(
                { char: Char, y: Int -> char == '#' } to STEEL_CASING,
                { char: Char, y: Int -> char == '@' } to STEEL_CASING_PIPE
            )

        override val hatches: HatchFlags
            get() = HatchFlags.Builder()
                .with(
                    HatchTypes.ITEM_INPUT,
                    HatchTypes.FLUID_INPUT,
                    HatchTypes.ITEM_OUTPUT,
                    HatchTypes.ENERGY_INPUT
                )
                .build()

        override val hatchPredicate: Map<(Char, Int) -> Boolean, HatchFlags>
            get() = mapOf(
                { char: Char, _: Int -> char == '#' } to hatches
            )

        override val controllerXOffset = -1

        override val controllerYOffset = -1

        val SHAPE = ShapeTemplate.Builder(MachineCasings.STEEL)
            .addLayer(0)
            .addLayer(1)
            .addLayer(2)
            .build()
    }

    private val colorizer = ColorizerComponent()
    private val colorizerRecipe = ColorizerRecipeComponent(inventory, colorizer, this)

    init {
        registerComponents(colorizer, colorizerRecipe)
        registerGuiComponent(
            SlotPanel(this).withRedstoneControl(redstoneControl),
            ColorizerGui(colorizer, { colorizer.colors })
        )
    }

    override fun getFlexibleComponent() = colorizerRecipe

}
