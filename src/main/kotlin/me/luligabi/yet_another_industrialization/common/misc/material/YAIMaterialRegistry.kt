package me.luligabi.yet_another_industrialization.common.misc.material

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.minecraft.world.level.block.entity.BlockEntityType
import net.swedz.tesseract.neoforge.material.MaterialRegistry
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder

object YAIMaterialRegistry: MaterialRegistry() {

    init {
        YAIMaterials
    }

    override fun modId() = YAI.ID

    override fun blockRegistry() = YAIBlocks.Registry.BLOCKS

    override fun blockEntityRegistry() = YAIBlocks.Registry.BLOCK_ENTITIES

    override fun itemRegistry() = YAIItems.Registry.ITEMS

    override fun onBlockRegister(holder: BlockHolder<*>) {
        YAIBlocks.Registry.include(holder)
    }

    override fun onBlockEntityRegister(type: BlockEntityType<*>) {
    }

    override fun onItemRegister(holder: ItemHolder<*>) {
        YAIItems.Registry.include(holder)
    }

}