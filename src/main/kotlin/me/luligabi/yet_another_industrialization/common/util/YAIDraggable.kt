package me.luligabi.yet_another_industrialization.common.util

import aztech.modern_industrialization.compat.viewer.ReiDraggable
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import net.minecraft.world.item.ItemStack

/**
 * [ReiDraggable] if it had a `stack` argument :)
 */
interface YAIDraggable {

    fun dragItem(stack: ItemStack, itemKey: ItemVariant, simulation: Boolean): Boolean

    fun dragFluid(stack: ItemStack, fluidKey: FluidVariant, simulation: Boolean): Boolean
}
