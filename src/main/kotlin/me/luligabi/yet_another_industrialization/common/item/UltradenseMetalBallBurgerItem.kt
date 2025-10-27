package me.luligabi.yet_another_industrialization.common.item

import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class UltradenseMetalBallBurgerItem(properties: Properties) : Item(
    properties.food(
        FoodProperties.Builder()
            .nutrition(5).saturationModifier(1f)
            .effect({ MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 8 * 20, 2) }, 0.1f)
            .build()
    )
) {

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
        tooltip.add(YAIText.ULTRADENSE_METAL_BALL_BURGER_TOOLTIP.text().setStyle(TextHelper.GRAY_TEXT))
    }

}