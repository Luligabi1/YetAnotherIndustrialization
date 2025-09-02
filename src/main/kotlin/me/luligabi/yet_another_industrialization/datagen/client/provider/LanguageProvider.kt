package me.luligabi.yet_another_industrialization.datagen.client.provider

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks

class LanguageProvider(event: GatherDataEvent): LanguageProvider(event.generator.packOutput, YAI.ID, "en_us") {
    
    override fun addTranslations() {
        add("itemGroup.${YAI.ID}.${YAI.ID}", "Yet Another Industrialization!")

        for (item in YAIItems.values()) {
            add(item.asItem(), item.identifier().englishName())
        }

        for (fluid in YAIFluids.values()) {
            add(fluid.block().get(), fluid.identifier().englishName())
        }

        for (sound in SoundProvider.TRANSLATIONS) {
            add(sound.key, sound.value)
        }

        for (text in YAIText.entries) {
            add(text.translationKey, text.englishText())
        }

        MIDatagenHooks.Client.withLanguageHook(this, YAI.ID)
    }

}