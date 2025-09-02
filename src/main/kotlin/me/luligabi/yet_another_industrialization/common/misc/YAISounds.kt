package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

@Suppress("unused")
object YAISounds {

    private val SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, YAI.ID)

    internal fun init(bus: IEventBus) {
        SOUNDS.register(bus)
    }


    val MACHINE_REMOVER_REMOVE = createVariableRangeEvent("machine_remover_remove")

    private fun createVariableRangeEvent(id: String): Supplier<SoundEvent> {
        return SOUNDS.register(id, Supplier { SoundEvent.createVariableRangeEvent(YAI.id(id)) })
    }

}