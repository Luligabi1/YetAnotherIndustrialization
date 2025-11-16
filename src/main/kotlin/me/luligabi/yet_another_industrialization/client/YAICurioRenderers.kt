package me.luligabi.yet_another_industrialization.client

import me.luligabi.yet_another_industrialization.client.renderer.item.IndustrialistsGogglesCurioRenderer
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import top.theillusivec4.curios.api.client.CuriosRendererRegistry

object YAICurioRenderers {

    fun init(modEventBus: IEventBus) {
        modEventBus.register(this)
    }

    @SubscribeEvent
    private fun onClientSetup(event: FMLClientSetupEvent) {
        CuriosRendererRegistry.register(YAIItems.INDUSTRIALISTS_GOGGLES.get(), { IndustrialistsGogglesCurioRenderer })
    }

}