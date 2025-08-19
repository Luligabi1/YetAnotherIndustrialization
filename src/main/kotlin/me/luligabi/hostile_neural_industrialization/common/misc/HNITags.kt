package me.luligabi.hostile_neural_industrialization.common.misc

import me.luligabi.hostile_neural_industrialization.common.HNI
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey

object HNITags {

    val NETHERRACK_SOILS = TagKey.create(Registries.BLOCK, HNI.id("netherrack_soils"))

    val MYCELLIUMS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "myceliums"))

}