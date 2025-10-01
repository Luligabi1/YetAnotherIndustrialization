package me.luligabi.yet_another_industrialization.datagen.server

import me.luligabi.yet_another_industrialization.datagen.DatagenEntrypoint
import me.luligabi.yet_another_industrialization.datagen.server.provider.DataMapProvider
import me.luligabi.yet_another_industrialization.datagen.server.provider.YAILootTableProvider
import me.luligabi.yet_another_industrialization.datagen.server.provider.recipe.RecipeProvider
import me.luligabi.yet_another_industrialization.datagen.server.provider.tag.BlockTagProvider
import me.luligabi.yet_another_industrialization.datagen.server.provider.tag.ItemTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.neoforge.data.event.GatherDataEvent

object ServerDatagen: DatagenEntrypoint {

    fun onGatherData(event: GatherDataEvent) {
        event.add(::RecipeProvider)
        event.add(::BlockTagProvider)
        event.add(::ItemTagProvider)
        event.add(::DataMapProvider)
        event.addLootTable(::YAILootTableProvider)
    }

    private fun GatherDataEvent.addLootTable(providerCreator: (HolderLookup.Provider) -> LootTableSubProvider) {
        generator.addProvider(
            includeServer(),
            LootTableProvider(
                generator.packOutput,
                setOf(),
                listOf(SubProviderEntry(providerCreator, LootContextParamSets.BLOCK)),
                lookupProvider
            )
        )
    }

}