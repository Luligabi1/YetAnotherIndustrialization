package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerDragPacket
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot

fun sendDragPacket(menu: AbstractContainerMenu, slot: Slot, item: ItemVariant?, fluid: FluidVariant?): Boolean {
    (slot.item.item as? YAIDraggable)?.let { slotItem ->
        val slotId = menu.slots.indexOf(slot)
        if (item != null && slotItem.dragItem(slot.item, item, true)) {
            SlotLockerDragPacket(menu.containerId, slotId, item).sendToServer()
            return true
        }
        if (fluid != null && slotItem.dragFluid(slot.item, fluid, true)) {
            SlotLockerDragPacket(menu.containerId, slotId, fluid).sendToServer()
            return true
        }
    }
    return false
}