package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.MITooltips
import aztech.modern_industrialization.MITooltips.EU_MAXED_PARSER
import aztech.modern_industrialization.MITooltips.NumberWithMax
import aztech.modern_industrialization.api.energy.EnergyApi
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.item.IndustrialistsGogglesItem
import me.luligabi.yet_another_industrialization.common.item.tools.MachineDiagnoserItem
import me.luligabi.yet_another_industrialization.common.item.tools.MachineRemoverItem
import me.luligabi.yet_another_industrialization.common.item.tools.StorageSlotLockerItem
import net.minecraft.core.registries.BuiltInRegistries
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment
import java.util.*

@Suppress("unused")
object YAITooltips {

    private val SNEAK_RIGHT_CLICK = YAI.TEXT.sneakRCActivate2(YAI.TEXT.sneakRCActivate1())

    val ENERGY_STORED_ITEM: TooltipAttachment = TooltipAttachment.singleLineOptional(
        { stack, item -> BuiltInRegistries.ITEM.getKey(item).namespace == YAI.ID },
        { flags, context, stack, item ->
            stack.getCapability(EnergyApi.ITEM)?.let {
                val capacity = it.capacity
                if (capacity > 0) {
                    return@singleLineOptional Optional.of(
                        MIText.EnergyStored.text(
                            EU_MAXED_PARSER.parse(
                                NumberWithMax(
                                    it.amount,
                                    capacity
                                )
                            )
                        ).withStyle(MITooltips.DEFAULT_STYLE)
                    )
                }
            }
            Optional.empty()
        }
    ).noShiftRequired()

    val MACHINE_DIAGNOSER = TooltipAttachment.multilines(
        MachineDiagnoserItem::class.java,
        listOf(
            YAI.TEXT.diagnoserTooltip1(),
            SNEAK_RIGHT_CLICK
        )
    )

    val STORAGE_SLOT_LOCKER = TooltipAttachment.multilines(
        StorageSlotLockerItem::class.java,
        listOf(
            YAI.TEXT.slotLockerTooltip2(),
            YAI.TEXT.slotLockerTooltip3Suffix(YAI.TEXT.slotLockerTooltip3Instruction()),
            SNEAK_RIGHT_CLICK
        )
    )

    val MACHINE_REMOVER = TooltipAttachment.multilines(
        MachineRemoverItem::class.java,
        listOf(
            YAI.TEXT.machineRemoverTooltip1(),
            YAI.TEXT.machineRemoverTooltip2(MITooltips.EU_PARSER.parse(MachineRemoverItem.SINGLE_BLOCK_REMOVE_COST)),
            YAI.TEXT.machineRemoverTooltip3("${MachineRemoverItem.MULTIBLOCK_REMOVE_BASE_COST} + ${MachineRemoverItem.MULTIBLOCK_REMOVE_BLOCK_COST} * n EU"),
            SNEAK_RIGHT_CLICK
        )
    )

    val INDUSTRIALISTS_GOGGLES = TooltipAttachment.multilines(
        IndustrialistsGogglesItem::class.java,
        listOf(
            YAI.TEXT.gogglesTooltip1(),
            YAI.TEXT.gogglesTooltip2("${YAI.ID}.toggle_industrialists_goggles")
        )
    )

    val LARGE_STORAGE_UNIT = TooltipAttachment.multilines(
        listOf(YAI.id(LargeStorageUnitBlockEntity.ID)),
        listOf(
            YAI.TEXT.largeStorageUnitTooltip1(),
            YAI.TEXT.largeStorageUnitTooltip2()
        )
    )

}