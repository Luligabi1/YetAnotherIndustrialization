package me.luligabi.hostile_neural_industrialization.datagen.client.provider

import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.item.HNIItems
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
import me.luligabi.hostile_neural_industrialization.common.util.HNIText
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks

class HNILanguageProvider(event: GatherDataEvent): LanguageProvider(event.generator.packOutput, HNI.ID, "en_us") {
    
    override fun addTranslations() {
        add("itemGroup.${HNI.ID}.${HNI.ID}", "Yet Another Industrialization")

        for (item in HNIItems.values()) {
            add(item.asItem(), item.identifier().englishName())
        }

        for (fluid in HNIFluids.values()) {
            add(fluid.block().get(), fluid.identifier().englishName())
        }

        for (text in HNIText.entries) {
            add(text.translationKey, text.englishText())
        }

        MIDatagenHooks.Client.withLanguageHook(this, HNI.ID)
    }

}