package me.luligabi.yet_another_industrialization.common.misc.proxy

import io.wispforest.accessories.api.AccessoriesCapability
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment

@ProxyEntrypoint(priority = 1, environment = [ProxyEnvironment.MOD], modid = ["accessories"])
class YAIAccessoriesSlotProxy : YAIModSlotProxy() {

    override val isLoaded: Boolean
        get() = true

    override fun getContents(player: Player, filter: (ItemStack) -> Boolean): List<ItemStack> {
        val contents = mutableListOf<ItemStack>()

        AccessoriesCapability.get(player)?.let {
            for (entry in it.getEquipped(filter)) {
                contents.add(entry.stack())
            }
        }

        return contents.toList()
    }
}