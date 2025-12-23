package me.luligabi.yet_another_industrialization.common.block.machine.generator

import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity
import aztech.modern_industrialization.machines.components.CrafterComponent
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.components.RedstoneControlComponent
import aztech.modern_industrialization.machines.guicomponents.CraftingMultiblockGui
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.util.Simulation
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel

abstract class YAIGeneratorMultiblockBlockEntity(
    bep: BEP,
    id: ResourceLocation,
    orientationParams: OrientationComponent.Params,
    shape: Array<ShapeTemplate>
): AbstractCraftingMultiblockBlockEntity(bep, id, orientationParams, shape), CrafterComponent.Behavior, EnergyListComponentHolder {

    constructor(bep: BEP, id: ResourceLocation, shape: Array<ShapeTemplate>):
            this(bep, id, OrientationComponent.Params(false, false, false), shape)

    private val energyOutputs = mutableListOf<EnergyComponent>()
    private val redstoneControl = RedstoneControlComponent()

    init {
        registerComponents(redstoneControl)

        registerGuiComponent(
            SlotPanel(this)
                .withRedstoneControl(redstoneControl)
        )
        registerGuiComponent(CraftingMultiblockGui(
            { shapeValid.shapeValid },
            { crafter.progress },
            crafter,
            { 0 }
        ))
    }

    override fun onCraft() {
        val condition = (crafter as CrafterComponentAccessor).activeRecipe?.value()?.conditions
            ?.filterIsInstance<EnergyGenerationCondition>()?.firstOrNull() ?: return
        val amount = condition.amount

        val insertEnergy = insertEnergy(amount, true) > 0
        if (insertEnergy) {
            insertEnergy(amount, false)
        }
        onInsert(insertEnergy)
    }

    open fun onInsert(hasInsertedEnergy: Boolean) {
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

    final override fun onRematch(shapeMatcher: ShapeMatcher) {
        super.onRematch(shapeMatcher)
        if (shapeMatcher.isMatchSuccessful) {
            energyOutputs.clear()
            shapeMatcher.matchedHatches.forEach { it.appendEnergyOutputs(energyOutputs) }
            onSuccessfulRematch(shapeMatcher)
        } else {
            onFailedRematch(shapeMatcher)
        }
    }

    open fun onSuccessfulRematch(shapeMatcher: ShapeMatcher) {}

    open fun onFailedRematch(shapeMatcher: ShapeMatcher) {}

    override fun consumeEu(max: Long, simulation: Simulation) = max

    override fun getBaseRecipeEu() = 0L

    override fun getMaxRecipeEu() = 1L

    override fun getCrafterWorld() = level as? ServerLevel

    override fun getOwnerUuid() = placedBy.placerId

    override fun getBehavior() = this

    override fun getEnergyComponents() = energyOutputs
}