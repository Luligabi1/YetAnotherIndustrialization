package me.luligabi.yet_another_industrialization.common.item

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class AISlopItem(properties: Properties) : Item(
    properties.food(
        FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.4f)
            .build()
    )
) {

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
        tooltip.add(YAI.TEXT.aiSlopTooltip())
    }

}