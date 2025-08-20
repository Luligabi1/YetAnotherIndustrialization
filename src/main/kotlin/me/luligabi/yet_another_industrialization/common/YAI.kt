package me.luligabi.yet_another_industrialization.common

import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.compat.guideme.YAIGuide
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAICreativeTab
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.YAITooltips
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import me.luligabi.yet_another_industrialization.common.misc.network.YAIPackets
import me.luligabi.yet_another_industrialization.datagen.YAIDatagen
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent
import net.swedz.tesseract.neoforge.config.ConfigManager


@Mod(YAI.ID)
class YAI(modEventBus: IEventBus, container: ModContainer) {

    companion object {
        const val ID = "yet_another_industrialization"

        fun id(id: String) = ResourceLocation.fromNamespaceAndPath(ID, id)

        lateinit var CONFIG: YAIConfig
            private set

    }

    init {
        setupConfig(modEventBus, container)

        YAIItems.init(modEventBus)
        YAIBlocks.init(modEventBus)
        YAIFluids.init(modEventBus)
        YAIMachines.RecipeTypes.init(modEventBus)
        modEventBus.addListener(RegisterPayloadHandlersEvent::class.java, YAIPackets::init)
        YAICreativeTab.init(modEventBus)
        YAITooltips
        YAIGuide


        modEventBus.addListener(RegisterDataMapTypesEvent::class.java, YAIDataMaps::init)
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DataMapsUpdatedEvent::class.java) {
            it.ifRegistry(Registries.BLOCK) { _ -> ArboreousGreenhouseBlockEntity.initTiers() }
        }

        modEventBus.register(YAIDatagen)
    }

    private fun setupConfig(bus: IEventBus, container: ModContainer) {
        val manager = ConfigManager().includeDefaultValueComments()

        CONFIG = manager
            .build(YAIConfig::class.java)
            .register(container, ModConfig.Type.STARTUP)
            .load()
            .listenToLoad(bus)
            .config()
    }

}