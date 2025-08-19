package me.luligabi.hostile_neural_industrialization.common.item

import dev.shadowsoffire.placebo.util.EnchantmentUtils
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShearsItem
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

// used to grant correct items on AG recipes generated from loot tables
class ChaChaRealSmoothItem(properties: Properties): Item(properties
    .component(DataComponents.TOOL, ShearsItem.createToolProperties()
)) {

    override fun isCorrectToolForDrops(stack: ItemStack, state: BlockState) = true

    companion object {

        fun create(level: Level): ItemStack {
            val itemStack = ItemStack(HNIItems.CHA_CHA_REAL_SMOOTH).apply {

                val registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT) ?: throw IllegalStateException("Null enchantment registry... how")
                val silkTouch = registry.wrapAsHolder(registry.get(Enchantments.SILK_TOUCH))

                enchant(silkTouch, 1)
            }

            return itemStack
        }

    }

}