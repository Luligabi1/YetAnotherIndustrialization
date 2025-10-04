package me.luligabi.yet_another_industrialization.common.misc.datamap

import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

data class ArboreousGreenhouseSapling(
    var lootData: List<LootData>,
    val tier: ResourceLocation,
    var model: ResourceLocation,
) {

    companion object {
        val CODEC = RecordCodecBuilder.create { i ->
            i.group(
                LootData.CODEC.listOf().fieldOf("loot_data").forGetter(ArboreousGreenhouseSapling::lootData),
                ResourceLocation.CODEC.optionalFieldOf("tier", ArboreousGreenhouseTier.DEFAULT_TIER).forGetter(ArboreousGreenhouseSapling::tier),
                ResourceLocation.CODEC.fieldOf("model").forGetter(ArboreousGreenhouseSapling::model),
            ).apply(i, ::ArboreousGreenhouseSapling)
        }

        fun all() = BuiltInRegistries.ITEM.getDataMap(YAIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING)

    }

    data class LootData(val lootTable: ResourceLocation, val amount: Int, val probability: Float) {

        companion object {

            val CODEC = RecordCodecBuilder.create { i ->
                i.group(
                    ResourceLocation.CODEC.fieldOf("loot_table").forGetter(LootData::lootTable),
                    Codec.INT.optionalFieldOf("amount", 1).forGetter(LootData::amount),
                    MIExtraCodecs.FLOAT_01.optionalFieldOf("probability", 1f).forGetter(LootData::probability),
                ).apply(i, ::LootData)
            }

            fun getAmount(category: String) = when (category) {
                "log" -> logAmount()
                "leaves" -> leavesAmount()
                "other" -> specialAmount()
                else -> saplingAmount()
            }

            fun logAmount() = 16
            fun leavesAmount() = 32
            fun saplingAmount() = 1
            fun specialAmount() = 1
        }

    }

}