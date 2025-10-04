package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.MITooltips
import aztech.modern_industrialization.MITooltips.EU_MAXED_PARSER
import aztech.modern_industrialization.MITooltips.NumberWithMax
import aztech.modern_industrialization.api.energy.EnergyApi
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.item.MachineDiagnoserItem
import me.luligabi.yet_another_industrialization.common.item.MachineRemoverItem
import me.luligabi.yet_another_industrialization.common.item.StorageSlotLockerItem
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.line
import net.swedz.tesseract.neoforge.tooltip.Parser
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment
import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum
import java.util.*


object YAITooltips {

    private val HIGHLIGHTED_STRING = Parser { text: String ->
        Component.literal(text).withStyle(TextHelper.NUMBER_TEXT)
    }

    private val HIGHLIGHTED_TEXT = Parser { text: TranslatableTextEnum ->
        HIGHLIGHTED_COMPONENT.parse(text.text())
    }

    private val HIGHLIGHTED_COMPONENT = Parser { component: MutableComponent ->
        component.withStyle(TextHelper.NUMBER_TEXT)
    }

    private val SNEAK_RIGHT_CLICK = line(YAIText.SNEAK_RC_ACTIVATE_2).arg(YAIText.SNEAK_RC_ACTIVATE_1.text(), HIGHLIGHTED_COMPONENT)


    val ENERGY_STORED_ITEM: TooltipAttachment = TooltipAttachment.singleLineOptional(
        { stack, item -> BuiltInRegistries.ITEM.getKey(item).namespace == YAI.ID },
        { flags, context, stack, item ->
            stack.getCapability(EnergyApi.ITEM)?.let {
                val capacity = it.capacity
                if (capacity > 0) {
                    return@singleLineOptional Optional.of(
                        line(MIText.EnergyStored)
                            .arg(NumberWithMax(it.amount, capacity), EU_MAXED_PARSER)
                    )
                }
            }
            Optional.empty()
        }
    ).noShiftRequired()

    val MACHINE_DIAGNOSER = TooltipAttachment.multilines(
        MachineDiagnoserItem::class.java,
        listOf(
            line(YAIText.DIAGNOSER_TOOLTIP_1),
            SNEAK_RIGHT_CLICK
        )
    )


    val STORAGE_SLOT_LOCKER = TooltipAttachment.multilines(
        StorageSlotLockerItem::class.java,
        listOf(
            line(YAIText.SLOT_LOCKER_TOOLTIP_2),
            line(YAIText.SLOT_LOCKER_TOOLTIP_3_SUFFIX).arg(YAIText.SLOT_LOCKER_TOOLTIP_3_INSTRUCTION.text(), HIGHLIGHTED_COMPONENT),
            SNEAK_RIGHT_CLICK
        )
    )

    val MACHINE_REMOVER = TooltipAttachment.multilines(
        MachineRemoverItem::class.java,
        listOf(
            line(YAIText.MACHINE_REMOVER_TOOLTIP_1),
            line(YAIText.MACHINE_REMOVER_TOOLTIP_2).arg(
                MachineRemoverItem.SINGLE_BLOCK_REMOVE_COST,
                MITooltips.EU_PARSER
            ),
            line(YAIText.MACHINE_REMOVER_TOOLTIP_3)
                .arg(
                    "${MachineRemoverItem.MULTIBLOCK_REMOVE_BASE_COST} + ${MachineRemoverItem.MULTIBLOCK_REMOVE_BLOCK_COST} * n EU",
                    HIGHLIGHTED_STRING
                ),
            SNEAK_RIGHT_CLICK
        )
    )

    val LARGE_STORAGE_UNIT = TooltipAttachment.multilines(
        listOf(YAI.id(LargeStorageUnitBlockEntity.ID)),
        listOf(
            line(YAIText.LARGE_STORAGE_UNIT_TOOLTIP_1),
            line(YAIText.LARGE_STORAGE_UNIT_TOOLTIP_2)
        )
    )

}