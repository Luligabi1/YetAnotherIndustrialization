package me.luligabi.yet_another_industrialization.common.misc.datamap

import com.mojang.serialization.Codec
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.datamaps.DataMapType
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

object YAIDataMaps {

    private val DATA_MAPS = hashSetOf<DataMapType<*, *>>()

    val ARBOREOUS_GREENHOUSE_SAPLING = create("arboreous_greenhouse_sapling", Registries.ITEM, ArboreousGreenhouseSapling.CODEC, true)
    val ARBOREOUS_GREENHOUSE_TIER = create("arboreous_greenhouse_tier", Registries.BLOCK, ArboreousGreenhouseTier.CODEC, true)

    private fun <R, T> create(
        name: String,
        registry: ResourceKey<Registry<R>>,
        codec: Codec<T>,
        sync: Boolean
    ): DataMapType<R, T> {
        var builder = DataMapType.builder(YAI.id(name), registry, codec)
        if (sync) {
            builder = builder.synced(codec, true)
        }
        val type = builder.build()
        DATA_MAPS.add(type)
        return type
    }

    fun init(event: RegisterDataMapTypesEvent) {
        DATA_MAPS.forEach { event.register(it) }
    }

}