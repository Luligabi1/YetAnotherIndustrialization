package me.luligabi.yet_another_industrialization.common.block.machine.misc

import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.AutoExtract
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes

class MixedHatch(
    bep: BEP,
    machineId: String,
    private val input: Boolean,
    private val upgradesToSteel: Boolean,
    private val inventory: MIInventory,
    backgroundHeight: Int
) : HatchBlockEntity(
    bep,
    MachineGuiParameters.Builder(machineId, true).backgroundHeight(backgroundHeight).build(),
    OrientationComponent.Params(true, true, true)
) {

    init {
        registerComponents(inventory)
        registerGuiComponent(AutoExtract.Server(orientation, input))
    }

    override fun appendItemInputs(list: MutableList<ConfigurableItemStack>) {
        if (input) {
            list.addAll(inventory.itemStacks)
        }
    }

    override fun appendItemOutputs(list: MutableList<ConfigurableItemStack>) {
        if (!input) {
            list.addAll(inventory.itemStacks)
        }
    }

    override fun appendFluidInputs(list: MutableList<ConfigurableFluidStack>) {
        if (input) {
            list.addAll(inventory.fluidStacks)
        }
    }

    override fun appendFluidOutputs(list: MutableList<ConfigurableFluidStack>) {
        if (!input) {
            list.addAll(inventory.fluidStacks)
        }
    }

    override fun tickTransfer() {
        if (orientation.extractItems) {
            if (input) {
                inventory.autoInsertItems(level, worldPosition, orientation.outputDirection)
            } else {
                inventory.autoExtractItems(level, worldPosition, orientation.outputDirection)
            }
        }
        if (orientation.extractFluids) {
            if (input) {
                inventory.autoInsertFluids(level, worldPosition, orientation.outputDirection)
            } else {
                inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection)
            }
        }
    }

    override fun getHatchType() = if (input) YAIHatchTypes.MIXED_INPUT else YAIHatchTypes.MIXED_OUTPUT

    override fun upgradesToSteel() = upgradesToSteel

    override fun getInventory() = inventory

}