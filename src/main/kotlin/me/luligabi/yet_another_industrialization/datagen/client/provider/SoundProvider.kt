package me.luligabi.yet_another_industrialization.datagen.client.provider

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAISounds
import net.minecraft.resources.ResourceLocation
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
            .with(sound(YAI.id("machine_remover_remove")))

        addWithSubtitle(YAISounds.STORAGE_SLOT_LOCKER_LOCK.get(), "Storage Slot Locker activates")
            .with(sound(ResourceLocation.withDefaultNamespace("block/iron_trapdoor/close1")).volume(0.9f))
            .with(sound(ResourceLocation.withDefaultNamespace("block/iron_trapdoor/close2")).volume(0.9f))
            .with(sound(ResourceLocation.withDefaultNamespace("block/iron_trapdoor/close3")).volume(0.9f))
            .with(sound(ResourceLocation.withDefaultNamespace("block/iron_trapdoor/close4")).volume(0.9f))
    }

    private fun add(soundEvent: SoundEvent): SoundDefinition {
        val result = definition()
        add(soundEvent, result)
        return result
    }

    private fun addWithSubtitle(soundEvent: SoundEvent, englishTranslation: String): SoundDefinition {
        val key = "${YAI.ID}.subtitle.${soundEvent.location.path}"
        TRANSLATIONS[key] = englishTranslation
        return add(soundEvent).subtitle(key)
    }

}