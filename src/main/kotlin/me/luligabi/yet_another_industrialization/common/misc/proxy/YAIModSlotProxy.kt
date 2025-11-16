package me.luligabi.yet_another_industrialization.common.misc.proxy

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.swedz.tesseract.neoforge.proxy.Proxies
import net.swedz.tesseract.neoforge.proxy.Proxy
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint


@ProxyEntrypoint
open class YAIModSlotProxy : Proxy {

    open val isLoaded: Boolean
        get() = false

    open fun getContents(player: Player, filter: (ItemStack) -> Boolean): List<ItemStack> {
        return emptyList()
    }

    companion object {

        fun getAllItems(player: Player): List<ItemStack> {
            val inventory = player.getInventory()

            val items = mutableListOf<ItemStack>()
            items.addAll(inventory.armor)
            items.addAll(inventory.items)
            items.addAll(inventory.offhand)
            items.addAll(Proxies.get(YAIModSlotProxy::class.java).getContents(player, { true }))

            return items
        }

        fun getHeadItems(player: Player, filter: (ItemStack) -> Boolean): List<ItemStack> {
            val inventory = player.getInventory()

            val items = mutableListOf<ItemStack>()
            inventory.armor[3].let {
                if (filter(it)) items.add(it)
            }
            items.addAll(Proxies.get(YAIModSlotProxy::class.java).getContents(player, filter))

            return items
        }

    }

}