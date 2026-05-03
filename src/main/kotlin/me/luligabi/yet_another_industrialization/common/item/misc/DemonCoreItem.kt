package me.luligabi.yet_another_industrialization.common.item.misc

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DemonCoreItem(properties: Properties): Item(properties.durability(512)) {

    override fun isEnchantable(stack: ItemStack) = false

    override fun isBookEnchantable(stack: ItemStack, book: ItemStack) = false

}