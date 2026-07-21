package me.luligabi.yet_another_industrialization.common.misc.datamap

import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

data class NumismaticGeneratorCurrency(
    val euPerItem: Long
) {

    companion object {

        val CODEC = RecordCodecBuilder.create {
            it.group(
                MIExtraCodecs.POSITIVE_LONG.fieldOf("eu_per_item").forGetter(NumismaticGeneratorCurrency::euPerItem)
            ).apply(it, ::NumismaticGeneratorCurrency)
        }

        fun all() = BuiltInRegistries.ITEM.getDataMap(YAIDataMaps.NUMISMATIC_GENERATOR_CURRENCY)

        fun getEu(item: Item): Long {
            return item.builtInRegistryHolder().getData(YAIDataMaps.NUMISMATIC_GENERATOR_CURRENCY)?.euPerItem ?: 0L
        }

    }
}