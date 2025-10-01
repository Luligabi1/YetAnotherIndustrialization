package me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition
import aztech.modern_industrialization.util.TextHelper
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import me.luligabi.yet_another_industrialization.common.util.matchedHatches
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

class EnergyGenerationCondition(val amount: Long): MachineProcessCondition {

    companion object {

        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.LONG.fieldOf("amount").forGetter(EnergyGenerationCondition::amount)
            ).apply(it, ::EnergyGenerationCondition)
        }

        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, EnergyGenerationCondition::amount,
            ::EnergyGenerationCondition
        )

    }

    override fun canProcessRecipe(ctx: MachineProcessCondition.Context, recipe: MachineRecipe): Boolean {
        val multiblock = ctx.blockEntity as? MultiblockMachineBlockEntity ?: return false
        return multiblock.matchedHatches.allows(HatchTypes.ENERGY_OUTPUT)
    }

    override fun codec() = CODEC

    override fun streamCodec() = STREAM_CODEC

    override fun appendDescription(list: MutableList<Component>) {
        val amount = TextHelper.getAmount(amount)
        list.add(
            YAIText.ENERGY_GENERATION_TOOLTIP_1.text(
                MIText.Eu.text(amount.digit, amount.unit).applyStyle(TextHelper.NUMBER_TEXT)
            )
        )
    }

    override fun icon() = ItemStack(YAIItems.ENERGY_ZAP)

}