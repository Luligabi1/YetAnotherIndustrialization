package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.rei

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import me.luligabi.yet_another_industrialization.common.compat.recipeviewer.sendDragPacket
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable
import me.luligabi.yet_another_industrialization.mixin.AbstractContainerScreenAccessor
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.drag.DraggableStack
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult
import me.shedaniel.rei.api.client.gui.drag.DraggingContext
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.stream.Stream

class StorageSlotLockerDraggableStackVisitor : DraggableStackVisitor<Screen> {

    override fun acceptDraggedStack(
        context: DraggingContext<Screen>,
        stack: DraggableStack
    ): DraggedAcceptorResult {
        return if (acceptsStack(context, stack)) DraggedAcceptorResult.ACCEPTED else DraggedAcceptorResult.PASS
    }

    private fun acceptsStack(context: DraggingContext<Screen>, stack: DraggableStack): Boolean {
        val ik = (stack.stack.getValue() as? ItemStack)?.let { ItemVariant.of(it) }
        val fk = (stack.stack.getValue() as? FluidStack)?.let { FluidVariant.of(FluidStackHooksForge.toForge(it)) }

        (context.getScreen() as? AbstractContainerScreen<*>)?.let { screen ->
            val slot = (screen as AbstractContainerScreenAccessor).hoveredSlot ?: return false
            val handler = screen.getMenu()

            return sendDragPacket(handler, slot, ik, fk)
        }
        return false
    }

    override fun getDraggableAcceptingBounds(context: DraggingContext<Screen>, stack: DraggableStack): Stream<DraggableStackVisitor.BoundsProvider> {
        val bounds = mutableListOf<DraggableStackVisitor.BoundsProvider>()

        val ik = (stack.stack.getValue() as? ItemStack)?.let { ItemVariant.of(it) }
        val fk = (stack.stack.getValue() as? FluidStack)?.let { FluidVariant.of(FluidStackHooksForge.toForge(it)) }

        (context.getScreen() as? AbstractContainerScreen<*>)?.let { screen ->
            val handler = screen.getMenu()
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
        }
        return bounds.stream()
    }

    override fun <R : Screen> isHandingScreen(screen: R) = screen !is CreativeModeInventoryScreen

    private companion object {

         fun getSlotBounds(slot: Slot, screen: AbstractContainerScreen<*>): DraggableStackVisitor.BoundsProvider {
            return DraggableStackVisitor.BoundsProvider.ofRectangle(
                Rectangle(
                    slot.x + screen.guiLeft,
                    slot.y + screen.guiTop,
                    16,
                    16
                )
            )
        }
    }
}