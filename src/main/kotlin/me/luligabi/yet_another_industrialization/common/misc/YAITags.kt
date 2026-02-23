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
    val NON_MYCELIUM_DIRT = commonBlockTag("dirts/non_mycelium")
    val NETHERRACK_SOILS = TagKey.create(Registries.BLOCK, YAI.id("netherrack_soils"))
    val MYCELLIUMS = commonBlockTag("myceliums")

    /** Biome */
    val SCORCHING_LIQUID_AIR_BIOMES = commonTag(Registries.BIOME, "scorching_liquid_air_biomes")
    val GELID_LIQUID_AIR_BIOMES = commonTag(Registries.BIOME, "gelid_liquid_air_biomes")

    /** Block Entity */
    val MACHINE_REMOVER_BANNED = TagKey.create(Registries.BLOCK_ENTITY_TYPE, YAI.id("machine_remover_banned"))

    private fun commonBlockTag(id: String) = commonTag(Registries.BLOCK, id)

    private fun commonItemTag(id: String) = commonTag(Registries.ITEM, id)

    private fun <T> commonTag(registry: ResourceKey<Registry<T>>, id: String) = TagKey.create<T>(registry, ResourceLocation.fromNamespaceAndPath("c", id))

}