package me.luligabi.yet_another_industrialization.common.block.machine.misc

import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.inventory.SlotPositions
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.AutoExtract
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.util.Tickable
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines

class ConfigurableMixedStorageMachineBlockEntity(bep: BEP) : MachineBlockEntity(
    bep, MachineGuiParameters.Builder(YAI.id(ID), true).backgroundHeight(162).build(),
    OrientationComponent.Params(true, true, true)
), Tickable {

    companion object {

        const val ID = "configurable_mixed_storage"
        const val NAME = "Configurable Mixed Storage"

    }

    private val inventory: MIInventory

    init {
        val itemStacks = List(9) { ConfigurableItemStack.standardIOSlot(true) }
        val fluidStacks = List(9) { ConfigurableFluidStack.standardIOSlot(16_000, true) }

        val itemPositions = SlotPositions.Builder().addSlots(8, 30, 9, 1).build()
        val fluidPositions = SlotPositions.Builder().addSlots(8, 48, 9, 1).build()

        inventory = MIInventory(itemStacks, fluidStacks, itemPositions, fluidPositions)

        registerGuiComponent(AutoExtract.Server(orientation))
        registerComponents(inventory)
    }

    override fun getInventory() = inventory

    override fun getMachineModelData(): MachineModelClientData {
        val data = MachineModelClientData(YAIMachines.Casings.CONFIGURABLE_MIXED_STORAGE)
        orientation.writeModelData(data)
        return data
    }

    override fun tick() {
        if (level!!.isClientSide()) return

        if (orientation.extractItems) {
            inventory.autoExtractItems(level, worldPosition, orientation.outputDirection)
        }
        if (orientation.extractFluids) {
            inventory.autoExtractFluids(level, worldPosition, orientation.outputDirection)
        }
    }
}
