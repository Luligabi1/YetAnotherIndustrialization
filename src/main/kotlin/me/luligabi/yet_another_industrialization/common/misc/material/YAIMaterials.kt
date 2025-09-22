package me.luligabi.yet_another_industrialization.common.misc.material

import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import net.swedz.tesseract.neoforge.compat.mi.material.recipe.MIMaterialRecipeGroups
import net.swedz.tesseract.neoforge.material.builtin.part.MaterialParts

object YAIMaterials {

    val BATTERY_ALLOY = MIMaterials.BATTERY_ALLOY.`as`(YAIMaterialRegistry)
        .add(MIMaterialParts.ROD)
        .add(MIMaterialParts.RING)
        .add(MIMaterialParts.BOLT)
        .add(MIMaterialParts.GEAR)
        .add(MIMaterialParts.MACHINE_CASING)
        .recipes(MIMaterialRecipeGroups.STANDARD_MACHINE_CASING)

    val CADMIUM = MIMaterials.CADMIUM.`as`(YAIMaterialRegistry)
        .add(MaterialParts.BLOCK)

    fun values() = setOf(BATTERY_ALLOY, CADMIUM)
}