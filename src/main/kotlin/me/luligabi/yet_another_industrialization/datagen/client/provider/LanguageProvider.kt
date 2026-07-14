package me.luligabi.yet_another_industrialization.datagen.client.provider

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.PulseDetonationGeneratorBlockEntity
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.misc.effect.YAIEffects
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

        add(YAIEffects.CREATIVE_FLIGHT.get().descriptionId, "Creative Flight")
        add("${YAIEffects.CREATIVE_FLIGHT.get().descriptionId}.description", "Allows user to fly as if in Creative Mode while in range of an active Flight Pylon.")

        for (sound in SoundProvider.TRANSLATIONS) {
            add(sound.key, sound.value)
        }

        this.add(YAI.id(PulseDetonationGeneratorBlockEntity.ID).toLanguageKey("death.attack"), "%1\$s tried to hug their Pulse Detonation Generator")
        this.add(YAI.id(PulseDetonationGeneratorBlockEntity.ID).toLanguageKey("death.attack") + ".player", "%1\$s hoped their Pulse Detonation Generator would protect them from %2\$s")

        YAITags.FUEL_ROD_TRANSLATIONS.forEach { (tag, translation) ->
            add(tag, translation)
        }

        YAI.LANG_INSTANCE.datagen(this)
        MIDatagenHooks.Client.withLanguageHook(this, YAI.ID)
    }

}