package me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch

import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.inventory.SlotPositions
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import net.minecraft.resources.ResourceLocation

class ItemTrashCanHatch(bep: BEP, input: Boolean, blockId: ResourceLocation) : HatchBlockEntity(
    bep,
    MachineGuiParameters.Builder(blockId, true).build(),
    OrientationComponent.Params(false, false, false)
) {

    companion object {

        const val ID = "item_trash_can_hatch"
        const val NAME = "Item Trash Can Hatch"
    }

    private val inventory = run {
        val stacks = List(2) { ConfigurableItemStack() }

        MIInventory(
            stacks, emptyList(),
            SlotPositions.Builder().addSlots(80, 30, 1, 2).build(),
            SlotPositions.empty()
        )
    }

    init {
        registerComponents(inventory)
    }

    override fun tickTransfer() {
        inventory.itemStacks.forEach {
            it.amount = 0
        }
    }

    override fun appendItemOutputs(list: MutableList<ConfigurableItemStack>) {
        list.addAll(inventory.itemStacks)
    }

    override fun getHatchType() = HatchTypes.ITEM_OUTPUT

    override fun upgradesToSteel() = true

    override fun getInventory() = inventory

}