package me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch

import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.inventory.SlotPositions
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import net.minecraft.resources.ResourceLocation

class FluidTrashCanHatch(bep: BEP, input: Boolean, blockId: ResourceLocation) : HatchBlockEntity(
    bep,
    MachineGuiParameters.Builder(blockId, true).build(),
    OrientationComponent.Params(false, false, false)
) {

    companion object {

        const val ID = "fluid_trash_can_hatch"
        const val NAME = "Fluid Trash Can Hatch"
    }

    private val inventory = run {
        val stack = listOf(ConfigurableFluidStack(16_000))

        MIInventory(
            emptyList(), stack,
            SlotPositions.empty(),
            SlotPositions.Builder().addSlot(80, 40).build()
        )
    }

    init {
        registerComponents(inventory)
    }

    override fun tickTransfer() {
        inventory.fluidStacks.forEach {
            it.amount = 0
        }
    }

    override fun appendFluidOutputs(list: MutableList<ConfigurableFluidStack>) {
        list.addAll(inventory.fluidStacks)
    }

    override fun getHatchType() = HatchTypes.FLUID_OUTPUT

    override fun upgradesToSteel() = true

    override fun getInventory() = inventory

}