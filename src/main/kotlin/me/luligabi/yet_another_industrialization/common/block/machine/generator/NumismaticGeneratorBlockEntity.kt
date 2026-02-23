package me.luligabi.yet_another_industrialization.common.block.machine.generator

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.blockentities.GeneratorMachineBlockEntity
import aztech.modern_industrialization.machines.components.FluidItemConsumerComponent
import me.luligabi.yet_another_industrialization.common.misc.datamap.NumismaticGeneratorCurrency
import net.minecraft.world.item.Item

class NumismaticGeneratorBlockEntity(
    bep: BEP
): GeneratorMachineBlockEntity(bep, ID, true, CableTier.LV, 1_048_576L, 0L, CONSUMER) {

    companion object {

        const val ID = "numismatic_generator"
        const val NAME = "Numismatic Generator"

        private val CONSUMER: FluidItemConsumerComponent
            get() = FluidItemConsumerComponent(1_048_576, PRODUCTION_MAP, FluidItemConsumerComponent.EUProductionMap.empty())

        private val PRODUCTION_MAP = object : FluidItemConsumerComponent.EUProductionMap<Item> {

            override fun getEuProduction(item: Item) = NumismaticGeneratorCurrency.getEu(item)

            override fun getNumberOfFuel() = FluidItemConsumerComponent.NumberOfFuel.MANY

            override fun getAllAccepted() = emptyList<Item>()

        }
    }

}