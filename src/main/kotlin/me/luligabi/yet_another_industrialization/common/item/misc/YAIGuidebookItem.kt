package me.luligabi.yet_another_industrialization.common.item.misc

import guideme.GuidesCommon
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.compat.guideme.YAIGuide
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class YAIGuidebookItem(properties: Properties) : Item(properties) {

    override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (world.isClientSide) {
            GuidesCommon.openGuide(user, YAIGuide.ID)
        }

        return InteractionResultHolder.consume(user.getItemInHand(hand))
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag) {
        tooltip.add(YAI.TEXT.guidebookTooltip())
    }

}