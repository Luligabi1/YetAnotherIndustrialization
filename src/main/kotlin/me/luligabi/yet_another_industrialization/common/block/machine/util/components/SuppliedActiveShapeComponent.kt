package me.luligabi.yet_another_industrialization.common.block.machine.util.components

import aztech.modern_industrialization.machines.IComponent
import aztech.modern_industrialization.machines.components.ActiveShapeComponent
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag

/**
 * [ActiveShapeComponent], but [shapeTemplates] are supplied.
 * Used for multiblocks where the tiers are defined through data maps
 */
class SuppliedActiveShapeComponent(private val shapeTemplates: () -> Array<ShapeTemplate>): IComponent {

    var activeShape = 0
        private set

    fun incrementShape(machine: MultiblockMachineBlockEntity, delta: Int) {
        val newShape = activeShape + delta
        val capped = newShape.coerceIn(0, shapeTemplates().size - 1)
        setShape(machine, capped)
    }

    fun setShape(machine: MultiblockMachineBlockEntity, newShape: Int) {
        if (newShape != activeShape) {
            activeShape = newShape
            machine.setChanged()
            machine.unlink()
            machine.sync(false)
        }
    }

    override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider?) {
        tag.putInt("activeShape", activeShape)
    }

    override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider?, isUpgradingMachine: Boolean) {
        activeShape = tag.getInt("activeShape").coerceAtMost(shapeTemplates().size - 1)
    }

}