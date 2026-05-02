package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

object YAITags {

    /** Item */
    val MI_GUIDE_BOOKS = commonItemTag("modern_industrialization/guide_books")

    /** Block */
    val GRASS_SOILS = yaiBlockTag("grass_soils")
    val NETHERRACK_SOILS = yaiBlockTag("netherrack_soils")
    val MYCELLIUMS = commonBlockTag("myceliums")

    /** Biome */
    val SCORCHING_LIQUID_AIR_BIOMES = commonTag(Registries.BIOME, "scorching_liquid_air_biomes")
    val GELID_LIQUID_AIR_BIOMES = commonTag(Registries.BIOME, "gelid_liquid_air_biomes")

    /** Block Entity */
    val MACHINE_REMOVER_BANNED = yaiTag(Registries.BLOCK_ENTITY_TYPE, "machine_remover_banned")

    private fun commonItemTag(id: String) = commonTag(Registries.ITEM, id)

    private fun commonBlockTag(id: String) = commonTag(Registries.BLOCK, id)

    private fun <T> commonTag(registry: ResourceKey<Registry<T>>, id: String) = TagKey.create<T>(registry, ResourceLocation.fromNamespaceAndPath("c", id))

    private fun yaiItemTag(id: String) = yaiTag(Registries.ITEM, id)

    private fun yaiBlockTag(id: String) = yaiTag(Registries.BLOCK, id)

    private fun <T> yaiTag(registry: ResourceKey<Registry<T>>, id: String) = TagKey.create<T>(registry, YAI.id(id))

}