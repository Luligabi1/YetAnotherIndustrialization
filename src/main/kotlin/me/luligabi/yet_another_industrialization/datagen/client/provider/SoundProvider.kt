package me.luligabi.yet_another_industrialization.datagen.client.provider

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAISounds
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.common.data.SoundDefinition
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

class SoundProvider(event: GatherDataEvent): SoundDefinitionsProvider(event.generator.packOutput, YAI.Companion.ID, event.existingFileHelper) {

    companion object {
        val TRANSLATIONS = hashMapOf<String, String>()
    }

    override fun registerSounds() {
        addWithSubtitle(YAISounds.MACHINE_REMOVER_REMOVE.get(), "Machine Remover activates")
            .with(sound(YAI.Companion.id("machine_remover_remove")))
    }

    private fun add(soundEvent: SoundEvent): SoundDefinition {
        val result = definition()
        add(soundEvent, result)
        return result
    }

    private fun addWithSubtitle(soundEvent: SoundEvent, englishTranslation: String): SoundDefinition {
        val key = "${YAI.Companion.ID}.subtitle.${soundEvent.location.path}"
        TRANSLATIONS[key] = englishTranslation
        return add(soundEvent).subtitle(key)
    }

}