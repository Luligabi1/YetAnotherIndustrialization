package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.jei

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import me.luligabi.yet_another_industrialization.common.compat.recipeviewer.sendDragPacket
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable
import mezz.jei.api.gui.handlers.IGhostIngredientHandler
import mezz.jei.api.ingredients.ITypedIngredient
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.Rect2i
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack

class StorageSlotLockerGhostIngredientHandler : IGhostIngredientHandler<AbstractContainerScreen<*>> {

    override fun <I> getTargetsTyped(
        gui: AbstractContainerScreen<*>,
        typedIngredient: ITypedIngredient<I>,
        doStart: Boolean
    ): MutableList<IGhostIngredientHandler.Target<I>> {
        val ingredient = typedIngredient.getIngredient()
        val bounds: MutableList<IGhostIngredientHandler.Target<I>> = ArrayList()

        val ik = if (ingredient is ItemStack) ItemVariant.of(ingredient) else null
        val fk = if (ingredient is FluidStack) FluidVariant.of(ingredient) else null

        val handler = gui.getMenu()
        for (slot in handler.slots) {
            (slot.item.item as? YAIDraggable)?.let {
                if (ik != null && it.dragItem(slot.item, ik, true)) {
                    bounds.add(object : IGhostIngredientHandler.Target<I> {

                        override fun getArea() = getSlotTarget(slot, gui)

                        override fun accept(ingredient: I) {
                            if (it.dragItem(slot.item, ik, false)) {
                               sendDragPacket(handler, slot, ik, fk)
                            }
                        }
                    })
                }
                if (fk != null && it.dragFluid(slot.item, fk, true)) {
                    bounds.add(object : IGhostIngredientHandler.Target<I> {

                        override fun getArea() = getSlotTarget(slot, gui)

                        override fun accept(ingredient: I) {
                            if (it.dragFluid(slot.item, fk, false)) {
                                sendDragPacket(handler, slot, ik, fk)
                            }
                        }
                    })
                }
            }
        }

        return bounds
    }

    override fun onComplete() {
    }

    private companion object {
        fun getSlotTarget(slot: Slot, screen: AbstractContainerScreen<*>): Rect2i {
            return Rect2i(slot.x + screen.guiLeft, slot.y + screen.guiTop, 16, 16)
        }
    }
}