package me.luligabi.yet_another_industrialization.datagen.server.provider.tag

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

class BlockTagProvider(event: GatherDataEvent): BlockTagsProvider(event.generator.packOutput, event.lookupProvider, YAI.ID, event.existingFileHelper) {

    override fun addTags(provider: HolderLookup.Provider) {
        for (block in YAIBlocks.values().sortedBy { it.identifier().id() }) {
            for (tag in block.tags()) {
                tag(tag).add(block.get())
            }
        }

        tag(YAITags.NETHERRACK_SOILS)
            .addTag(Tags.Blocks.NETHERRACKS)
            .addTag(BlockTags.NYLIUM)
    }

}