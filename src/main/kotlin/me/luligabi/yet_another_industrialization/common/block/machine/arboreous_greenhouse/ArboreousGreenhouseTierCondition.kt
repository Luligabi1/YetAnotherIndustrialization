package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse

import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessCondition
import aztech.modern_industrialization.util.TextHelper
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class ArboreousGreenhouseTierCondition(
    val tierId: ResourceLocation,
    val model: ResourceLocation
): MachineProcessCondition {

    companion object {

        val CODEC = RecordCodecBuilder.mapCodec {
            it.group(
                ResourceLocation.CODEC.fieldOf("tier_id").forGetter(ArboreousGreenhouseTierCondition::tierId),
                ResourceLocation.CODEC.fieldOf("model").forGetter(ArboreousGreenhouseTierCondition::model)
            ).apply(it, ::ArboreousGreenhouseTierCondition)
        }

        val STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ArboreousGreenhouseTierCondition::tierId,
            ResourceLocation.STREAM_CODEC, ArboreousGreenhouseTierCondition::model,
            ::ArboreousGreenhouseTierCondition
        )
    }

    override fun canProcessRecipe(context: MachineProcessCondition.Context, recipe: MachineRecipe): Boolean {
        return (context.blockEntity as? ArboreousGreenhouseBlockEntity)?.let {
            ArboreousGreenhouseBlockEntity.TIERS.getOrNull(it.activeSoil.activeShape)?.id == tierId
        } ?: false
    }

    override fun codec() = CODEC

    override fun streamCodec() = STREAM_CODEC

    override fun icon(): ItemStack {
        val iconId = ArboreousGreenhouseTier.get(tierId)?.icon ?: return ItemStack.EMPTY

        BuiltInRegistries.BLOCK.get(ResourceKey.create(Registries.BLOCK, iconId))?.let {
            return ItemStack(it)
        }
        BuiltInRegistries.ITEM.get(ResourceKey.create(Registries.ITEM, iconId))?.let {
            return ItemStack(it)
        }
        return ItemStack.EMPTY
    }

    override fun appendDescription(list: MutableList<Component>) {
        val tierName = ArboreousGreenhouseTier.get(tierId)?.translationKey ?: return

        list.add(
            YAIText.ARBOREOUS_GREENHOUSE_TIER_TOOLTIP.text(
                Component.translatable(tierName).applyStyle(TextHelper.NUMBER_TEXT)
            ).applyStyle(TextHelper.GRAY_TEXT)
        )
    }

}