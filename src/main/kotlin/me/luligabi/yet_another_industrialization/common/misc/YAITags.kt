package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

object YAITags {

    val TRANSLATIONS = mutableMapOf<TagKey<*>, String>()

    /** Item */
    val MI_GUIDE_BOOKS = commonItemTag("modern_industrialization/guide_books", "MI Guide Books")

    val MI_FUEL_RODS_URANIUM = yaiItemTag("fuel_rods/uranium", "Uranium Fuel Rods")
    val MI_FUEL_RODS_LE_URANIUM = yaiItemTag("fuel_rods/le_uranium", "LE Uranium Fuel Rods")
    val MI_FUEL_RODS_HE_URANIUM = yaiItemTag("fuel_rods/he_uranium", "HE Uranium Fuel Rods")
    val MI_FUEL_RODS_LE_MOX = yaiItemTag("fuel_rods/le_mox", "LE Mox Fuel Rods")
    val MI_FUEL_RODS_HE_MOX = yaiItemTag("fuel_rods/he_mox", "HE Mox Fuel Rods")

    val LIFESPAN_DURABILITY_TOOLTIP = yaiItemTag("lifespan_durability_tooltip", "Lifespan Durability Tooltip")

    /** Block */
    val GRASS_SOILS = yaiBlockTag("grass_soils", "Grass Soils")
    val NETHERRACK_SOILS = yaiBlockTag("netherrack_soils", "Netherrack Soils")
    val MYCELLIUMS = commonBlockTag("myceliums", "Mycelium Blocks")

    /** Biome */
    val OTHERWORLDLY_HOT_BIOMES = commonTag(Registries.BIOME, "otherworldly_hot_biomes", "Otherworldly Hot Biomes")
    val OTHERWORLDLY_COLD_BIOMES = commonTag(Registries.BIOME, "otherworldly_cold_biomes", "Otherworldly Cold Biomes")

    /** Block Entity */
    val MACHINE_REMOVER_BANNED = yaiTag(Registries.BLOCK_ENTITY_TYPE, "machine_remover_banned", "Machine Remover Banned")

    private fun commonItemTag(id: String, name: String? = null) = commonTag(Registries.ITEM, id, name)

    private fun commonBlockTag(id: String, name: String? = null) = commonTag(Registries.BLOCK, id, name)

    private fun yaiItemTag(id: String, name: String? = null) = yaiTag(Registries.ITEM, id, name)

    private fun yaiBlockTag(id: String, name: String? = null) = yaiTag(Registries.BLOCK, id, name)

    private fun <T> commonTag(registry: ResourceKey<Registry<T>>, id: String, name: String? = null) = tag(registry, "c", id, name)

    private fun <T> yaiTag(registry: ResourceKey<Registry<T>>, id: String, name: String? = null) = tag(registry, YAI.ID, id, name)

    private fun <T> tag(registry: ResourceKey<Registry<T>>, namespace: String, id: String, name: String? = null): TagKey<T> {
        val tag = TagKey.create<T>(registry, ResourceLocation.fromNamespaceAndPath(namespace, id))
        name?.let { TRANSLATIONS[tag] = it }
        return tag
    }

}