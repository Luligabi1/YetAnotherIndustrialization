package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.machines.IComponent.ServerOnly
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.components.DropableComponent
import dev.technici4n.grandpower.api.EnergyStorageUtil
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.swedz.tesseract.neoforge.compat.mi.api.ComponentStackHolder

class ChargingSlotComponent: ServerOnly, DropableComponent, ComponentStackHolder {

    companion object {
        val ID = YAI.id("charging_slot")

        const val KEY = "chargingSlotStack"

    }

    fun chargeItem(storage: MIEnergyStorage) {
        val itemStorage = chargingItem.getCapability(MIEnergyStorage.ITEM) ?: return
        if (!itemStorage.canReceive()) return
        EnergyStorageUtil.move(storage, itemStorage, itemStorage.capacity / 8)
    }

    var chargingItem: ItemStack = ItemStack.EMPTY
        private set

    fun hasItem() = !chargingItem.isEmpty

    fun setChargingItem(be: MachineBlockEntity, chargingItem: ItemStack) {
        stack = chargingItem
        be.setChanged()
        be.sync()
    }
    override fun getDrop() = chargingItem

    override fun getStack() = chargingItem

    override fun setStack(stack: ItemStack) {
        chargingItem = stack
    }

    override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put(KEY, chargingItem.saveOptional(registries))
    }

    override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider, isUpgradingMachine: Boolean) {
        chargingItem = ItemStack.parseOptional(registries, tag.getCompound(KEY))
    }

}