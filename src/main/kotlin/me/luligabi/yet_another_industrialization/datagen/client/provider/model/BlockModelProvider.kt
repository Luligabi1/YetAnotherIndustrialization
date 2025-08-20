package me.luligabi.yet_another_industrialization.datagen.client.provider.model

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


class BlockModelProvider(event: GatherDataEvent): BlockStateProvider(event.generator.packOutput, YAI.ID, event.existingFileHelper) {

    override fun registerStatesAndModels() {
        for (block in YAIBlocks.values()) {
            if (!block.hasModelProvider()) continue
            block.modelProvider().accept(this)
        }
    }

}