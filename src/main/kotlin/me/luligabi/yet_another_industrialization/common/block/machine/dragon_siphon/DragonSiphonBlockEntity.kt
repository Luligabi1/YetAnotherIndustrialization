package me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon

import aztech.modern_industrialization.MI
import aztech.modern_industrialization.MIBlock
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity
import aztech.modern_industrialization.machines.components.CrafterComponent
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.components.RedstoneControlComponent
import aztech.modern_industrialization.machines.guicomponents.CraftingMultiblockGui
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.multiblocks.*
import aztech.modern_industrialization.util.Simulation
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper.Companion.GLASS_MEMBER
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel

class DragonSiphonBlockEntity(bep: BEP): AbstractCraftingMultiblockBlockEntity(
    bep,
    ID,
    OrientationComponent.Params(false, false, false),
    arrayOf(SHAPE)
), CrafterComponent.Behavior, EnergyListComponentHolder {

    companion object : YAIMultiblockHelper {

        const val ID = "dragon_egg_energy_siphon"
        const val NAME = "Dragon Egg Energy Siphon"

        fun registerReiShapes() {
            ReiMachineRecipes.registerMultiblockShape(YAI.id(ID), SHAPE)
        }

        private val CASING = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS[MI.id("solid_titanium_machine_casing")])
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
                { char: Char, y: Int -> char == '@' } to GLASS_MEMBER,
                { char: Char, y: Int -> char == '$' } to DRAGON_EGG,
            )

        override val hatches: HatchFlags
            get() = HatchFlags.Builder()
                .with(
                    HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT,
                    HatchType.FLUID_INPUT, HatchType.FLUID_OUTPUT,
                    HatchType.ENERGY_OUTPUT
                )
                .build()

        override val hatchPredicate: (SimpleMember) -> Boolean
            get() = { it == CASING }

        override val controllerXOffset = -1

        private val SHAPE = ShapeTemplate.Builder(MachineCasings.TITANIUM)
            .addLayer(0)
            .addLayer(1)
            .addLayer(2)
            .build()
    }

    private val energyOutputs = mutableListOf<EnergyComponent>()
    private val redstoneControl = RedstoneControlComponent()

    init {
        registerComponents(redstoneControl)

        registerGuiComponent(
            SlotPanel.Server(this)
                .withRedstoneControl(redstoneControl)
        )
        registerGuiComponent(CraftingMultiblockGui.Server(
            { shapeValid.shapeValid },
            { crafter.progress },
            crafter,
            { 0 }
        ))
    }

    override fun onCraft() {
        val condition = (crafter as CrafterComponentAccessor).activeRecipe.value().conditions
            .filterIsInstance<EnergyGenerationCondition>().firstOrNull() ?: return
        val amount = condition.amount

        if (insertEnergy(amount, true) > 0) {
            insertEnergy(amount, false)
        }
    }

    override fun tickExtra() { // TODO particles?
        super.tickExtra()
    }

    fun insertEnergy(value: Long, simulate: Boolean): Long {
        var rem = value
        var inserted: Long = 0
        for (e in energyOutputs) {
            if (rem > 0) {
                inserted += e.insertEu(rem, if (simulate) Simulation.SIMULATE else Simulation.ACT)
                rem -= inserted
            }
        }
        return inserted
    }

    override fun onRematch(shapeMatcher: ShapeMatcher) {
        super.onRematch(shapeMatcher)
        if (shapeMatcher.isMatchSuccessful) {
            energyOutputs.clear()
            shapeMatcher.matchedHatches.forEach { it.appendEnergyOutputs(energyOutputs) }
        }
    }

    override fun recipeType() = YAIMachines.RecipeTypes.DRAGON_SIPHON

    override fun consumeEu(max: Long, simulation: Simulation) = max

    override fun getBaseRecipeEu() = 0L

    override fun getMaxRecipeEu() = 10L

    override fun getCrafterWorld() = level as? ServerLevel

    override fun getOwnerUuid() = placedBy.placerId

    override fun getBehavior() = this

    override fun getEnergyComponents() = energyOutputs
}