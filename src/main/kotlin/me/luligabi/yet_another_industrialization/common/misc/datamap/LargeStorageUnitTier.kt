package me.luligabi.yet_another_industrialization.common.misc.datamap

import aztech.modern_industrialization.MIBlockKeys
import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.mixin.CableTierAccessor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.swedz.tesseract.neoforge.helper.RegistryHelper

data class LargeStorageUnitTier(
    val capacity: Long,
    val cableTier: String,
    val translationKey: String
) {

    constructor(capacity: Long, cableTier: CableTier, translationKey: String) : this(
        capacity,
        cableTier.name,
        translationKey
    )

    companion object {

        private val CABLE_TIER_CODEC = Codec.STRING.flatXmap(
            { id ->
                parseTier(id)?.let {
                    DataResult.success(id)
                } ?: DataResult.error({ "Unknown cable tier: $id" })
            },
            { DataResult.success(it) }
        )

        val CODEC = RecordCodecBuilder.create {
            it.group(
                MIExtraCodecs.POSITIVE_LONG.fieldOf("capacity").forGetter(LargeStorageUnitTier::capacity),
                CABLE_TIER_CODEC.fieldOf("cable_tier").forGetter(LargeStorageUnitTier::cableTier),
                Codec.STRING.fieldOf("translation_key").forGetter(LargeStorageUnitTier::translationKey)
            ).apply(it, ::LargeStorageUnitTier)
        }

        fun all() = BuiltInRegistries.BLOCK.getDataMap(YAIDataMaps.LARGE_STORAGE_UNIT_TIER)

        fun getFor(block: Block): LargeStorageUnitTier? {
            return RegistryHelper.holder(BuiltInRegistries.BLOCK, block).getData(YAIDataMaps.LARGE_STORAGE_UNIT_TIER)
        }

        fun getHull(key: ResourceLocation, cableTier: CableTier): ResourceLocation {
            if (cableTier.name == "lv") return MIBlockKeys.BASIC_MACHINE_HULL.location()
            return cableTier.itemKey ?: key
        }

        fun parseTier(value: String): CableTier? {
            return if (value == "*") {
                CableTierAccessor.getTiers().values.maxBy { it.eu }
            } else {
                CableTierAccessor.getTiers()[value]
            }
        }

    }

    fun toRegisteredTier(key: ResourceKey<Block>) = LargeStorageUnitBlockEntity.Tier(
        key.location(),
        capacity,
        parseTier(cableTier) ?: error("Unknown cable tier: $cableTier"),
        translationKey
    )

}