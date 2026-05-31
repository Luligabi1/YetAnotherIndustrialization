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
    val GUIDE_BOOK_OR_BOOK = TagKey.create(Registries.ITEM, YAI.id("guide_book_or_book"))

    val MI_FUEL_RODS_URANIUM = yaiItemTag("fuel_rods/uranium")
    val MI_FUEL_RODS_LE_URANIUM = yaiItemTag("fuel_rods/le_uranium")
    val MI_FUEL_RODS_HE_URANIUM = yaiItemTag("fuel_rods/he_uranium")
    val MI_FUEL_RODS_LE_MOX = yaiItemTag("fuel_rods/le_mox")
    val MI_FUEL_RODS_HE_MOX = yaiItemTag("fuel_rods/he_mox")

    val LIFESPAN_DURABILITY_TOOLTIP = yaiItemTag("lifespan_durability_tooltip")

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

    val FUEL_ROD_TRANSLATIONS = hashMapOf(
        MI_FUEL_RODS_URANIUM to "Uranium Fuel Rods",
        MI_FUEL_RODS_LE_URANIUM to "LE Uranium Fuel Rods",
        MI_FUEL_RODS_HE_URANIUM to "HE Uranium Fuel Rods",
        MI_FUEL_RODS_LE_MOX to "LE Mox Fuel Rods",
        MI_FUEL_RODS_HE_MOX to "HE Mox Fuel Rods",

    )

    private fun commonBlockTag(id: String) = commonTag(Registries.BLOCK, id)

    private fun <T> commonTag(registry: ResourceKey<Registry<T>>, id: String) = TagKey.create<T>(registry, ResourceLocation.fromNamespaceAndPath("c", id))

    private fun yaiItemTag(id: String) = yaiTag(Registries.ITEM, id)

    private fun yaiBlockTag(id: String) = yaiTag(Registries.BLOCK, id)

    private fun <T> yaiTag(registry: ResourceKey<Registry<T>>, id: String) = TagKey.create<T>(registry, YAI.id(id))

}