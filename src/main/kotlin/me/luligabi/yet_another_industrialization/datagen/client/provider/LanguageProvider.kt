package me.luligabi.yet_another_industrialization.datagen.client.provider

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.misc.keybind.YAIKeybinds
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

        add(YAIKeybinds.CATEGORY, "Yet Another Industrialization!")
        for (keybind in YAIKeybinds.Registry.mappings) {
            add(keybind.descriptionId, keybind.englishName)
        }

        for (sound in SoundProvider.TRANSLATIONS) {
            add(sound.key, sound.value)
        }

        YAITags.FUEL_ROD_TRANSLATIONS.forEach { (tag, translation) ->
            add(tag, translation)
        }

        YAI.LANG_INSTANCE.datagen(this)
        MIDatagenHooks.Client.withLanguageHook(this, YAI.ID)
    }

}