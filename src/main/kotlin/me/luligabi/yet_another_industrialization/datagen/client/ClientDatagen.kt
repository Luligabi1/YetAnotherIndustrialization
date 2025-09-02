package me.luligabi.yet_another_industrialization.datagen.client

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.datagen.DatagenEntrypoint
import me.luligabi.yet_another_industrialization.datagen.client.provider.LanguageProvider
import me.luligabi.yet_another_industrialization.datagen.client.provider.SoundProvider
import me.luligabi.yet_another_industrialization.datagen.client.provider.model.BlockModelProvider
import me.luligabi.yet_another_industrialization.datagen.client.provider.model.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks

object ClientDatagen: DatagenEntrypoint {

    fun onGatherData(event: GatherDataEvent) {
        MIDatagenHooks.Client.addMachineCasingModelsHook(event, YAI.ID)
        MIDatagenHooks.Client.includeMISprites(event)
        MIDatagenHooks.Client.addTexturesHook(event, YAI.ID, YAIFluids.values())

        event.add(::BlockModelProvider)
        event.add(::ItemModelProvider)
        event.add(::SoundProvider)

        event.add(::LanguageProvider)
    }

}