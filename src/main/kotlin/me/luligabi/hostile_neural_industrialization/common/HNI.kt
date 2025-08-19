package me.luligabi.hostile_neural_industrialization.common

import me.luligabi.hostile_neural_industrialization.common.block.HNIBlocks
import me.luligabi.hostile_neural_industrialization.common.block.machine.HNIMachines
import me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.hostile_neural_industrialization.common.compat.guideme.HNIGuide
import me.luligabi.hostile_neural_industrialization.common.item.HNIItems
import me.luligabi.hostile_neural_industrialization.common.misc.HNICreativeTab
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
import me.luligabi.hostile_neural_industrialization.common.misc.HNIIngredients
import me.luligabi.hostile_neural_industrialization.common.misc.datamap.HNIDataMaps
import me.luligabi.hostile_neural_industrialization.common.misc.network.HNIPackets
import me.luligabi.hostile_neural_industrialization.datagen.HNIDatagen
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


@Mod(HNI.ID)
class HNI(modEventBus: IEventBus, container: ModContainer) {

    companion object {
        const val ID = "hostile_neural_industrialization"

        fun id(id: String) = ResourceLocation.fromNamespaceAndPath(ID, id)

        lateinit var CONFIG: HNIConfig
            private set

    }

    init {
        setupConfig(modEventBus, container)

        HNIItems.init(modEventBus)
        HNIBlocks.init(modEventBus)
        HNIFluids.init(modEventBus)
        HNIMachines.RecipeTypes.init(modEventBus)
        HNIIngredients.init(modEventBus)
        modEventBus.addListener(RegisterPayloadHandlersEvent::class.java, HNIPackets::init)
        HNICreativeTab.init(modEventBus)
        HNIGuide


        modEventBus.addListener(RegisterDataMapTypesEvent::class.java, HNIDataMaps::init)
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DataMapsUpdatedEvent::class.java) {
            it.ifRegistry(Registries.BLOCK) { _ -> ArboreousGreenhouseBlockEntity.initTiers() }
        }

        modEventBus.register(HNIDatagen)
    }

    private fun setupConfig(bus: IEventBus, container: ModContainer) {
        val manager = ConfigManager().includeDefaultValueComments()

        CONFIG = manager
            .build(HNIConfig::class.java)
            .register(container, ModConfig.Type.STARTUP)
            .load()
            .listenToLoad(bus)
            .config()
    }

}