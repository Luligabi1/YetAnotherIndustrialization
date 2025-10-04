package me.luligabi.yet_another_industrialization.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShearsItem
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.ItemAbility

// used to grant correct items on AG recipes generated from loot tables
class ChaChaRealSmoothItem(properties: Properties): Item(properties
    .component(DataComponents.TOOL, ShearsItem.createToolProperties()
)) {

    override fun isCorrectToolForDrops(stack: ItemStack, state: BlockState) = true

    override fun canPerformAction(stack: ItemStack, itemAbility: ItemAbility): Boolean {
        return itemAbility in ABILITIES
    }

    companion object {

        fun create(level: Level): ItemStack {
            val itemStack = ItemStack(YAIItems.CHA_CHA_REAL_SMOOTH).apply {

                val registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT) ?: throw IllegalStateException("Null enchantment registry... how")
                val silkTouch = registry.wrapAsHolder(registry.get(Enchantments.SILK_TOUCH))

                enchant(silkTouch, 1)
            }

            return itemStack
        }

        private val ABILITIES = setOf(
            ItemAbilities.SHEARS_DIG,
            ItemAbilities.AXE_DIG,
            ItemAbilities.PICKAXE_DIG,
            ItemAbilities.HOE_DIG,
            ItemAbilities.SHOVEL_DIG,
            ItemAbilities.SWORD_DIG
        )

    }

}