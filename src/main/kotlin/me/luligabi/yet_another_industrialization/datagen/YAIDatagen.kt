package me.luligabi.yet_another_industrialization.datagen

import me.luligabi.yet_another_industrialization.datagen.client.ClientDatagen
import me.luligabi.yet_another_industrialization.datagen.server.ServerDatagen
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.data.event.GatherDataEvent

object YAIDatagen {

    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent) {
        ServerDatagen.onGatherData(event)
        ClientDatagen.onGatherData(event)
    }

}