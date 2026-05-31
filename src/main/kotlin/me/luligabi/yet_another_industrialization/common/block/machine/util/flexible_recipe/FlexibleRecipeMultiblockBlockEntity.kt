package me.luligabi.yet_another_industrialization.common.block.machine.util.flexible_recipe

import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.ActiveShapeComponent
import aztech.modern_industrialization.machines.components.IsActiveComponent
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.util.Tickable

/**
 * Flexible recipes are recipes that don't conform to the MachineRecipe codec, likely
 * depending on external factors like a component's data or the inputs to determine what it'll output.
 */
abstract class FlexibleRecipeMultiblockBlockEntity(
    bep: BEP,
    guiParams: MachineGuiParameters,
    orientationParams: OrientationComponent.Params,
    shapeTemplates: Array<ShapeTemplate>
) : MultiblockMachineBlockEntity(
    bep,
    guiParams,
    orientationParams
), Tickable, MultiblockInventoryComponentHolder, FlexibleRecipeComponent.Behavior {

    private var operatingState = OperatingState.NOT_MATCHED

    protected val activeShape = ActiveShapeComponent(shapeTemplates)
    protected val inventory = MultiblockInventoryComponent()
    private val isActive = IsActiveComponent()

    init {
        registerComponents(activeShape, isActive)
    }

    abstract fun getFlexibleComponent(): FlexibleRecipeComponent

    override fun tick() {
        if (!level!!.isClientSide) {
            link()

            var newActive = false

            if (operatingState == OperatingState.TRYING_TO_RESUME) {
                if (getFlexibleComponent().tryContinueRecipe()) {
                    operatingState = OperatingState.NORMAL_OPERATION
                }
            }

            if (operatingState == OperatingState.NORMAL_OPERATION) {
                if (getFlexibleComponent().tick()) {
                    newActive = true
                }
            } /*else {
                crafter.decreaseEfficiencyTicks()
            }*/

            isActive.updateActive(newActive, this)
        }
        tickExtra()
    }

    open fun tickExtra() {
    }

    override fun onRematch(shapeMatcher: ShapeMatcher) {
        operatingState = OperatingState.NOT_MATCHED
        if (shapeMatcher.isMatchSuccessful) {
            inventory.rebuild(shapeMatcher)
            operatingState = OperatingState.TRYING_TO_RESUME
        }
    }

    override fun getActiveShape() = activeShape.activeShape

    override fun getMultiblockInventoryComponent() = inventory

    override fun getInventory() = MIInventory.EMPTY

    override fun getMachineModelData(): MachineModelClientData {
        return MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive)
    }

    private enum class OperatingState {
        NOT_MATCHED,
        TRYING_TO_RESUME,
        NORMAL_OPERATION
    }
}