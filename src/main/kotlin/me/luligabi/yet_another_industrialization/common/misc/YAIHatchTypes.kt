package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch
import me.luligabi.yet_another_industrialization.common.util.YAIText

object YAIHatchTypes {

    val LARGE_STORAGE_UNIT_INPUT = HatchTypes.register(YAI.id("large_storage_unit_input"), YAI.id(LargeStorageUnitHatch.ID_INPUT))
    val LARGE_STORAGE_UNIT_OUTPUT = HatchTypes.register(YAI.id("large_storage_unit_output"), YAI.id(LargeStorageUnitHatch.ID_OUTPUT))


    val MIXED_INPUT = HatchTypes.register(YAI.id("mixed_input"), YAIText.MIXED_INPUT_HATCH.text())
    val MIXED_OUTPUT = HatchTypes.register(YAI.id("mixed_output"), YAIText.MIXED_OUTPUT_HATCH.text())

}