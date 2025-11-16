package me.luligabi.yet_another_industrialization.common.misc.proxy

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.swedz.tesseract.neoforge.proxy.ProxyEntrypoint
import net.swedz.tesseract.neoforge.proxy.ProxyEnvironment
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler
import kotlin.jvm.optionals.getOrNull

@ProxyEntrypoint(environment = [ProxyEnvironment.MOD], modid = ["curios"])
class YAICuriosSlotProxy : YAIModSlotProxy() {

    override val isLoaded: Boolean
        get() = true

    override fun getContents(player: Player, filter: (ItemStack) -> Boolean): List<ItemStack> {
        val contents = mutableListOf<ItemStack>()

        CuriosApi.getCuriosInventory(player).getOrNull()?.let {
            it.curios.forEach { _, slot: ICurioStacksHandler ->
                val stacks = slot.stacks
                for (index in 0..<stacks.slots) {
                    val stack = stacks.getStackInSlot(index)
                    if (filter(stack)) {
                        contents.add(stack)
                    }
                }
            }
        }

        return contents.toList()
    }
}