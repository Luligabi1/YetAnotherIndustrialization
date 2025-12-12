package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.inventory.HackySlot
import aztech.modern_industrialization.inventory.SlotGroup
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.gui.GuiComponent.MenuFacade
import aztech.modern_industrialization.machines.gui.GuiComponentServer
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.Unit
import net.minecraft.world.item.ItemStack

class ChargingSlot(
    private val machine: MachineBlockEntity,
    private val charged: ChargingSlotComponent
): GuiComponentServer<Unit, Unit> {

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

    override fun getParams() = Unit.INSTANCE

    override fun extractData() = Unit.INSTANCE

    override fun getType() = TYPE

    companion object {

        val TYPE = GuiComponentServer.Type(
            YAI.id("charging_slot"),
            StreamCodec.unit(Unit.INSTANCE),
            StreamCodec.unit(Unit.INSTANCE)
        )

        const val SLOT_X = -21
        const val SLOT_Y = 76

        fun mayPlace(stack: ItemStack) = stack.getCapability(MIEnergyStorage.ITEM) != null

    }

}