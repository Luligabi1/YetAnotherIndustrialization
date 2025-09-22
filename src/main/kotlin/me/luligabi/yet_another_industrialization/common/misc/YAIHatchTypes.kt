package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch

object YAIHatchTypes {

    val LARGE_STORAGE_UNIT_INPUT = HatchTypes.register(YAI.id("large_storage_unit_input"), YAI.id(LargeStorageUnitHatch.ID_INPUT))
    val LARGE_STORAGE_UNIT_OUTPUT = HatchTypes.register(YAI.id("large_storage_unit_output"), YAI.id(LargeStorageUnitHatch.ID_OUTPUT))

}