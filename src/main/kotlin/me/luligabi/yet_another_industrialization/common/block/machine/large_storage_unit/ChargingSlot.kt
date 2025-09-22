package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.inventory.HackySlot
import aztech.modern_industrialization.inventory.SlotGroup
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.gui.GuiComponent
import aztech.modern_industrialization.machines.gui.GuiComponent.MenuFacade
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.item.ItemStack

object ChargingSlot {

    val ID = YAI.id("charging_slot")

    const val SLOT_X = -21
    const val SLOT_Y = 76

    fun mayPlace(stack: ItemStack) = stack.getCapability(MIEnergyStorage.ITEM) != null

    class Server(
        private val machine: MachineBlockEntity,
        private val charged: ChargingSlotComponent
    ) : GuiComponent.ServerNoData {

        override fun writeInitialData(buf: RegistryFriendlyByteBuf) {
        }

        override fun getId() = ID

        override fun setupMenu(menu: MenuFacade) {
            menu.addSlotToMenu(
                object : HackySlot(SLOT_X, SLOT_Y) {

                    override fun getRealStack(): ItemStack {
                        return charged.chargingItem
                    }

                    override fun setRealStack(itemStack: ItemStack) {
                        charged.setChargingItem(machine, itemStack)
                    }

                    override fun mayPlace(stack: ItemStack): Boolean {
                        return ChargingSlot.mayPlace(stack)
                    }

                },
                SlotGroup.CONFIGURABLE_STACKS
            )
        }
    }
}