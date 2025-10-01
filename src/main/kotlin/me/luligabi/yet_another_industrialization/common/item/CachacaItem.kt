package me.luligabi.yet_another_industrialization.common.item

import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*

class CachacaItem(properties: Properties) : Item(
    properties.food(
        FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.8f)
            .alwaysEdible().fast()
            .effect({ MobEffectInstance(MobEffects.CONFUSION, 8 * 20, 1) }, 0.35f)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .build()
    ).stacksTo(16)
) {

    override fun getUseAnimation(stack: ItemStack) = UseAnim.DRINK

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
        tooltip.add(YAIText.CACHACA_TOOLTIP.text().setStyle(TextHelper.GRAY_TEXT))
    }

}