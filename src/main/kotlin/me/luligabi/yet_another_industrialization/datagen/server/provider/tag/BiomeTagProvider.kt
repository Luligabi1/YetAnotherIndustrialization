package me.luligabi.yet_another_industrialization.datagen.server.provider.tag

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import net.minecraft.core.HolderLookup
import net.minecraft.data.tags.BiomeTagsProvider
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.data.event.GatherDataEvent

class BiomeTagProvider(event: GatherDataEvent): BiomeTagsProvider(event.generator.packOutput, event.lookupProvider, YAI.ID, event.existingFileHelper) {

    override fun addTags(lookupProvider: HolderLookup.Provider) {
        tag(YAITags.SCORCHING_LIQUID_AIR_BIOMES)
            .addTag(Tags.Biomes.IS_HOT)
            .remove(Tags.Biomes.IS_HOT_OVERWORLD)

        tag(YAITags.GELID_LIQUID_AIR_BIOMES)
            .addTag(Tags.Biomes.IS_COLD)
            .remove(Tags.Biomes.IS_COLD_OVERWORLD)
    }

}