package me.luligabi.yet_another_industrialization.common

import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.compat.guideme.YAIGuide
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.*
import me.luligabi.yet_another_industrialization.common.misc.component.YAIDataComponents
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import me.luligabi.yet_another_industrialization.common.misc.effect.YAIEffects
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterials
import me.luligabi.yet_another_industrialization.common.misc.network.YAIPackets
import me.luligabi.yet_another_industrialization.common.util.MACHINE_REMOVER_STYLE
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.datagen.YAIDatagen
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent
import net.swedz.tesseract.config.ConfigManager
import net.swedz.tesseract.neoforge.capabilities.CapabilitiesListeners
import net.swedz.tesseract.neoforge.compat.mi.TesseractMI
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess
import net.swedz.tesseract.neoforge.lang.LangInstance
import net.swedz.tesseract.neoforge.lang.LangManager
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder
import net.swedz.tesseract.neoforge.tooltip.Parser

@Mod(YAI.ID)
class YAI(modEventBus: IEventBus, container: ModContainer) {

    companion object {
        const val ID = "yet_another_industrialization"

        fun id(id: String) = ResourceLocation.fromNamespaceAndPath(ID, id)

        lateinit var CONFIG: YAIConfig
            private set
        lateinit var TEXT: YAIText
            private set

        lateinit var LANG_INSTANCE: LangInstance<YAIText>
            private set
    }

    init {
        preSetup(modEventBus, container)

        TesseractMI.init(ID)
        YAIItems.init(modEventBus)
        YAIBlocks.init(modEventBus)
        YAIFluids.init(modEventBus)
        YAIMaterials
        YAIMachines.RecipeTypes.init(modEventBus)
        YAIDataComponents.init(modEventBus)
        modEventBus.addListener(RegisterPayloadHandlersEvent::class.java, YAIPackets::init)
        YAIEffects.init(modEventBus)
        YAISounds.init(modEventBus)
        YAIVillagerTrades
        YAICreativeTab.init(modEventBus)
        YAIGuide
        YAICommand

        modEventBus.addListener(FMLCommonSetupEvent::class.java, {
            YAIItems.values().forEach(ItemHolder<*>::triggerRegistrationListener)
        })
        modEventBus.addListener(RegisterCapabilitiesEvent::class.java, { CapabilitiesListeners.triggerAll(ID, it) })

        modEventBus.addListener(RegisterDataMapTypesEvent::class.java, YAIDataMaps::init)
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DataMapsUpdatedEvent::class.java) {
            it.ifRegistry(Registries.BLOCK) { _ ->
                ArboreousGreenhouseBlockEntity.initTiers()
                FlightPylonBlockEntity.initTiers()
                LargeStorageUnitBlockEntity.initTiers()
            }
        }

        modEventBus.register(YAIDatagen)
    }

    private fun preSetup(bus: IEventBus, container: ModContainer) {
        val configInstance = ConfigManager(ModConfigFileAccess(container, ModConfig.Type.STARTUP))
            .build(YAIConfig::class.java)
        CONFIG = configInstance
            .load()
            .config()
        bus.addListener(FMLCommonSetupEvent::class.java) { _ -> configInstance.load(false) }

        LANG_INSTANCE = LangManager(ID)
            .style("gray", { -> Style.EMPTY.withColor(ChatFormatting.GRAY) })
            .style("gray_italic", { -> TextHelper.GRAY_TEXT })
            .style("green_neutron", { -> TextHelper.NEUTRONS })
            .style("red", { -> TextHelper.RED })
            .style("highlight", { -> TextHelper.NUMBER_TEXT })
            .style("machine_remover", { -> MACHINE_REMOVER_STYLE })
            .parser("keybind", String::class.java, { -> Parser.KEYBIND })
            .build(YAIText::class.java)
            .load()

        TEXT = LANG_INSTANCE
            .lang()
    }

}