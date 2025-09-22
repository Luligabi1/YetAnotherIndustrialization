package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.MITooltips
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.item.MachineDiagnoserItem
import me.luligabi.yet_another_industrialization.common.item.MachineRemoverItem
import me.luligabi.yet_another_industrialization.common.item.StorageSlotLockerItem
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine.line
import net.swedz.tesseract.neoforge.tooltip.Parser
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment
import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum

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

//    val MACHINE_REMOVER = TooltipAttachment.multilines(
//        MachineRemoverItem::class.java,
//        { _, _, _, _ ->
//            val lines = mutableListOf<MICompatibleTextLine>()
//            lines.add(line(YAIText.MACHINE_REMOVER_TOOLTIP_1)
//                .arg(MachineRemoverItem.SINGLE_BLOCK_REMOVE_COST, MITooltips.EU_PARSER))
//            lines.add(line(YAIText.MACHINE_REMOVER_TOOLTIP_2_PREFIX.arg(
//                YAIText.MACHINE_REMOVER_TOOLTIP_2_PREFIX.arg("")
//            )))
//            lines.add(line(YAIText.SNEAK_RC_ACTIVATE))
//            lines
//        }
//    )


}