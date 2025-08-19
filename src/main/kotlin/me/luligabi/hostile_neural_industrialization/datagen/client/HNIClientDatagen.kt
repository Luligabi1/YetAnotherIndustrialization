package me.luligabi.hostile_neural_industrialization.datagen.client

import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
import me.luligabi.hostile_neural_industrialization.datagen.HNIDatagenEntrypoint
import me.luligabi.hostile_neural_industrialization.datagen.client.provider.HNILanguageProvider
import me.luligabi.hostile_neural_industrialization.datagen.client.provider.model.HNIBlockModelProvider
import me.luligabi.hostile_neural_industrialization.datagen.client.provider.model.HNIItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks

object HNIClientDatagen: HNIDatagenEntrypoint {

    fun onGatherData(event: GatherDataEvent) {
        MIDatagenHooks.Client.addMachineCasingModelsHook(event, HNI.ID)
        MIDatagenHooks.Client.includeMISprites(event)
        MIDatagenHooks.Client.addTexturesHook(event, HNI.ID, HNIFluids.values())

        event.add(::HNIBlockModelProvider)
        event.add(::HNIItemModelProvider)

        event.add(::HNILanguageProvider)
    }

}