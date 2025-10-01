package me.luligabi.yet_another_industrialization.common.util

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.api.machine.component.EnergyAccess

object EmptyEnergyAccess: MIEnergyStorage, EnergyAccess {

    override fun receive(maxReceive: Long, simulate: Boolean) = 0L

    override fun extract(maxExtract: Long, simulate: Boolean) = 0L

    override fun getAmount() = 0L

    override fun getEu() = 0L

    override fun getCapacity() = 0L

    override fun canExtract() = true // either one has to be true for cables to connect

    override fun canReceive() = false

    override fun canConnect(cableTier: CableTier) = true

}