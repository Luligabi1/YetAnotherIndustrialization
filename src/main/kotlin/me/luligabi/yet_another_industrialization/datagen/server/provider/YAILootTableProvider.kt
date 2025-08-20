package me.luligabi.yet_another_industrialization.datagen.server.provider

import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags

class YAILootTableProvider(registries: HolderLookup.Provider): BlockLootSubProvider(emptySet(), FeatureFlags.VANILLA_SET, registries) {

    override fun getKnownBlocks() = YAIBlocks.values()
        .filter { it.hasLootTable() }
        .map { it.get() }
        .toList()

    override fun generate() {

        for (block in YAIBlocks.values()) {
            if (!block.hasLootTable()) continue
            add(block.get(), block.buildLootTable(this))
        }

    }

}