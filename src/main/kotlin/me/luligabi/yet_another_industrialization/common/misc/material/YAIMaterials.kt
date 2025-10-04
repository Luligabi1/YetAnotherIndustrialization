package me.luligabi.yet_another_industrialization.common.misc.material

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import net.swedz.tesseract.neoforge.compat.mi.material.recipe.MIMachineMaterialRecipeContext
import net.swedz.tesseract.neoforge.material.builtin.part.MaterialParts
import net.swedz.tesseract.neoforge.material.builtin.recipe.VanillaMaterialRecipeContext
import net.swedz.tesseract.neoforge.material.recipe.MaterialRecipeGroup

object YAIMaterials {

    private val GEARLESS_MACHINE_CASING = MaterialRecipeGroup.create(::MIMachineMaterialRecipeContext)
        .add("machine_casing_special") {
            it.machine(
                "machine_casing",
                MIMachineRecipeTypes.ASSEMBLER,
                8, 10*20,
                MIMaterialParts.MACHINE_CASING_SPECIAL,
                1,
                { b -> b.addPartInput(MIMaterialParts.PLATE, 8) }
            )
        }
        .then(::VanillaMaterialRecipeContext)
        .add("machine_casing_special") {
            it.shaped(
                MIMaterialParts.MACHINE_CASING_SPECIAL,
                1,
                { r -> r.add('p', MIMaterialParts.PLATE) },
                "ppp",
                "p p",
                "ppp"
            )
        }


    val BATTERY_ALLOY = MIMaterials.BATTERY_ALLOY.`as`(YAIMaterialRegistry)
        .add(MIMaterialParts.MACHINE_CASING_SPECIAL.formattingRaw("battery_casing", "Battery Casing"))
        .recipes(GEARLESS_MACHINE_CASING)

    val CADMIUM = MIMaterials.CADMIUM.`as`(YAIMaterialRegistry)
        .add(MaterialParts.BLOCK)

    fun values() = setOf(BATTERY_ALLOY, CADMIUM)

}