package me.luligabi.yet_another_industrialization.common.misc

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.MITooltips
import aztech.modern_industrialization.MITooltips.EU_MAXED_PARSER
import aztech.modern_industrialization.api.energy.EnergyApi
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine
import net.swedz.tesseract.neoforge.tooltip.TooltipAttachment
import java.util.*

object YAITooltips {

    val ENERGY_STORED_ITEM = TooltipAttachment.singleLineOptional(
        { _, item -> BuiltInRegistries.ITEM.getKey(item).namespace == YAI.ID },
        TooltipAttachment.SingleLineTooltipFunction { flags: TooltipFlag, context: Item.TooltipContext, stack: ItemStack, item: Item ->
            val energyStorage = stack.getCapability(EnergyApi.ITEM)
            if (energyStorage != null) {
                val capacity = energyStorage.capacity
                if (capacity > 0) {
                    return@SingleLineTooltipFunction Optional.of(
                        MICompatibleTextLine.line(MIText.EnergyStored)
                            .arg(MITooltips.NumberWithMax(energyStorage.amount, capacity), EU_MAXED_PARSER)
                    )
                }
            }
            Optional.empty()
        }
    ).noShiftRequired()

}