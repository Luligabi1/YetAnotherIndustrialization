package me.luligabi.yet_another_industrialization.datagen.server.util

import net.minecraft.resources.ResourceLocation

data class ArboreousGreenhouseSapling(
    var lootData: List<LootData>,
    val tier: ResourceLocation,
    var model: ResourceLocation,
) {

    data class LootData(val lootTable: ResourceLocation, val amount: Int, val probability: Float) {

        companion object {

            fun getAmount(category: String) = when (category) {
                "log" -> 16
                "leaves" -> 32
                "other" -> 1
                else -> 1
            }
        }

    }

}