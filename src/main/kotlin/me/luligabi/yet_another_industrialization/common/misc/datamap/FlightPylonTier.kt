package me.luligabi.yet_another_industrialization.common.misc.datamap

import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity
import me.luligabi.yet_another_industrialization.common.util.HEX_COLOR_CODEC
import me.luligabi.yet_another_industrialization.common.util.toDecimalColor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.swedz.tesseract.neoforge.helper.RegistryHelper

data class FlightPylonTier(
    val range: Double,
    val eu: Long,
    val translationKey: String,
    val beaconColor: Int
) {

    constructor(
        range: Double,
        eu: Long,
        translationKey: String,
        beaconColor: String
    ): this(range, eu, translationKey, beaconColor.toDecimalColor())

    companion object {

        val CODEC = RecordCodecBuilder.create {
            it.group(
                Codec.doubleRange(1.0, Double.MAX_VALUE).fieldOf("range").forGetter(FlightPylonTier::range),
                MIExtraCodecs.POSITIVE_LONG.fieldOf("eu").forGetter(FlightPylonTier::eu),
                Codec.STRING.fieldOf("translation_key").forGetter(FlightPylonTier::translationKey),
                HEX_COLOR_CODEC.optionalFieldOf("beacon_color", 0xFFFFFF).forGetter(FlightPylonTier::beaconColor),
            ).apply(it, ::FlightPylonTier)
        }

        fun all() = BuiltInRegistries.BLOCK.getDataMap(YAIDataMaps.FLIGHT_PYLON_TIER)

        fun getFor(block: Block): FlightPylonTier? {
            return RegistryHelper.holder(BuiltInRegistries.BLOCK, block).getData(YAIDataMaps.FLIGHT_PYLON_TIER)
        }

    }

    fun toRegisteredTier(key: ResourceKey<Block>) = FlightPylonBlockEntity.Tier(
        key.location(),
        range,
        eu,
        translationKey,
        beaconColor
    )

}