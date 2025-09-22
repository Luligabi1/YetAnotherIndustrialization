package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

object YAITags {

    val MI_GUIDE_BOOKS = commonItemTag("modern_industrialization/guide_books")

    val NETHERRACK_SOILS = TagKey.create(Registries.BLOCK, YAI.id("netherrack_soils"))
    val MYCELLIUMS = commonBlockTag("myceliums")

    val PLATES_BATTERY_ALLOY = commonItemTag("plates/battery_alloy")
    val GEARS_BATTERY_ALLOY = commonItemTag("gears/battery_alloy")
    val RODS_BATTERY_ALLOY = commonItemTag("rods/battery_alloy")

    val MACHINE_REMOVER_BANNED = TagKey.create(Registries.BLOCK_ENTITY_TYPE, YAI.id("machine_remover_banned"))

    private fun commonBlockTag(id: String) = commonTag(Registries.BLOCK, id)

    private fun commonItemTag(id: String) = commonTag(Registries.ITEM, id)

    private fun <T> commonTag(registry: ResourceKey<Registry<T>>, id: String) = TagKey.create<T>(registry, ResourceLocation.fromNamespaceAndPath("c", id))

}