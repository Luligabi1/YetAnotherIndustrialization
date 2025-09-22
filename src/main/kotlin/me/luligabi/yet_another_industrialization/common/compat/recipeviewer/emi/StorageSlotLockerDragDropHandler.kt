package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.emi

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import dev.emi.emi.api.EmiDragDropHandler
import dev.emi.emi.api.stack.EmiIngredient
import me.luligabi.yet_another_industrialization.common.compat.recipeviewer.sendDragPacket
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable
import me.luligabi.yet_another_industrialization.mixin.AbstractContainerScreenAccessor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.client.renderer.Rect2i
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack

class StorageSlotLockerDragDropHandler : EmiDragDropHandler<Screen> {

    override fun dropStack(screen: Screen, ingredient: EmiIngredient, mouseX: Int, mouseY: Int): Boolean {
        if (screen !is AbstractContainerScreen<*>) return false
        if (screen is CreativeModeInventoryScreen) return false

        val stack = ingredient.emiStacks[0]
        val ik = if (stack.key is Item) ItemVariant.of(stack.itemStack) else null
        val fk = (stack.key as? Fluid)?.let {
            FluidVariant.of(FluidStack(it.builtInRegistryHolder(), 1, stack.componentChanges))
        }

        val handler = screen.getMenu()
        val slot = (screen as AbstractContainerScreenAccessor).hoveredSlot ?: return false

        return sendDragPacket(handler, slot, ik, fk)
    }

    override fun render(
        screen: Screen,
        ingredient: EmiIngredient,
        guiGraphics: GuiGraphics,
        mouseX: Int, mouseY: Int,
        delta: Float
    ) {
        if (screen !is AbstractContainerScreen<*>) return
        if (screen is CreativeModeInventoryScreen) return

        val bounds: MutableList<Rect2i> = ArrayList()
        val stack = ingredient.emiStacks[0]

        val ik = if (stack.key is Item) ItemVariant.of(stack.itemStack) else null
        val fk = (stack.key as? Fluid)?.let {
            FluidVariant.of(FluidStack(it.builtInRegistryHolder(), 1, stack.componentChanges))
        }

        val handler: AbstractContainerMenu = screen.getMenu()
        for (slot in handler.slots) {
            (slot.item.item as? YAIDraggable)?.let {
                if (ik != null && it.dragItem(slot.item, ik, true)) {
                    bounds.add(getSlotBounds(slot, screen))
                }
                if (fk != null && it.dragFluid(slot.item, fk, true)) {
                    bounds.add(getSlotBounds(slot, screen))
                }
            }
        }

        for (b in bounds) {
            guiGraphics.fill(b.x, b.y, b.x + b.width, b.y + b.height, 0x8822BB33.toInt())
        }
    }

    private companion object {


        fun getSlotBounds(slot: Slot, screen: AbstractContainerScreen<*>): Rect2i {
            return Rect2i(slot.x + screen.guiLeft, slot.y + screen.guiTop, 16, 16)
        }
    }
}
