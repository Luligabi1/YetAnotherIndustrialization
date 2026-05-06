package me.luligabi.yet_another_industrialization.common.item.tools

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents
import me.luligabi.yet_another_industrialization.common.misc.proxy.YAIModSlotProxy
import me.luligabi.yet_another_industrialization.common.util.toComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Equipable
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class IndustrialistsGogglesItem(properties: Properties) : Item(
    properties
        .stacksTo(1)
        .component(YAIDataComponents.GOGGLES_ENABLED, true)
), Equipable {

    override fun getEquipmentSlot() = EquipmentSlot.HEAD

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        return swapWithEquipmentSlot(this, level, player, hand)
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag) {
        val enabled = stack.getOrDefault(YAIDataComponents.GOGGLES_ENABLED, true)
        tooltip.add(YAI.TEXT.enabledPrefix(enabled.toComponent()))
    }

    companion object {

        fun toggleMode(player: Player): Boolean? {
            val stack = YAIModSlotProxy.getHeadItems(
                player, { it.item is IndustrialistsGogglesItem }
            ).firstOrNull() ?: return null

            val oldState = stack.getOrDefault(YAIDataComponents.GOGGLES_ENABLED, true)
            stack.set(YAIDataComponents.GOGGLES_ENABLED, !oldState)
            return !oldState
        }

    }

}