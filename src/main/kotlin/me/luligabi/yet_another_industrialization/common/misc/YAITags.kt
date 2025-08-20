package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

object YAITags {

    val NETHERRACK_SOILS = TagKey.create(Registries.BLOCK, YAI.id("netherrack_soils"))

    val MYCELLIUMS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "myceliums"))

}