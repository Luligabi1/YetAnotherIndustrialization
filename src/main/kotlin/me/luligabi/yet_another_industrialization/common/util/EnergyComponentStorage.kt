package me.luligabi.yet_another_industrialization.common.util

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.util.Simulation

class EnergyComponentStorage(
    private val component: () -> EnergyComponent
): MIEnergyStorage {

    override fun receive(maxReceive: Long, simulate: Boolean): Long {
        return component().insertEu(maxReceive, if (simulate) Simulation.SIMULATE else Simulation.ACT)
    }

    override fun extract(maxExtract: Long, simulate: Boolean): Long {
        return component().consumeEu(maxExtract, if (simulate) Simulation.SIMULATE else Simulation.ACT)
    }

    override fun getAmount() = component().eu

    override fun getCapacity() = component().capacity

    override fun canExtract() = true

    override fun canReceive() = true

    override fun canConnect(cableTier: CableTier) = true
}