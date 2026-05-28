package me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon

import aztech.modern_industrialization.machines.MachineComponent
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag

class FlightPylonBeaconComponent: MachineComponent {

    var enabled = true

    override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.putBoolean(KEY, this.enabled)
    }

    override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider, isUpgradingMachine: Boolean) {
        this.enabled = tag.getBoolean(KEY)
    }

    private companion object {
        const val KEY = "FlightPylonBeacon"
    }

}