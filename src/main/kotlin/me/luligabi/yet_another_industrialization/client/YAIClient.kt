package me.luligabi.yet_another_industrialization.client

import aztech.modern_industrialization.client.machines.GuiComponentsClient
import aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER
import aztech.modern_industrialization.machines.MachineBlock
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import me.luligabi.yet_another_industrialization.client.component.ChargingSlotClient
import me.luligabi.yet_another_industrialization.client.component.LargeStorageUnitGuiClient
import me.luligabi.yet_another_industrialization.client.component.SuppliedShapeSelectionClient
import me.luligabi.yet_another_industrialization.client.component.ToggleCheckboxClient
import me.luligabi.yet_another_industrialization.client.model.YAIModelLoaders
import me.luligabi.yet_another_industrialization.client.renderer.ArboreousGreenhouseBER
import me.luligabi.yet_another_industrialization.client.renderer.DetonationChamberCasingBER
import me.luligabi.yet_another_industrialization.client.renderer.FlightPylonBER
import me.luligabi.yet_another_industrialization.client.renderer.item.StorageSlotLockerComponent
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.ChargingSlot
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitGui
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.item.tools.StorageSlotLockerItem
import me.luligabi.yet_another_industrialization.common.misc.keybind.YAIKeybinds
import me.luligabi.yet_another_industrialization.common.misc.network.SlotLockerChangeModePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.common.NeoForge
import net.swedz.tesseract.config.ConfigManager
import net.swedz.tesseract.neoforge.config.ModConfigFileAccess


@Mod(YAI.ID, dist = [Dist.CLIENT])
class YAIClient(modEventBus: IEventBus, container: ModContainer) {

    companion object {

        lateinit var CONFIG: YAIClientConfig
            private set

    }

    init {
        val configInstance = ConfigManager(ModConfigFileAccess(container, ModConfig.Type.CLIENT))
            .build(YAIClientConfig::class.java)
        CONFIG = configInstance
            .load()
            .config()
        modEventBus.addListener(FMLCommonSetupEvent::class.java) { _ -> configInstance.load(false) }

        GuiComponentsClient.register(LargeStorageUnitGui.TYPE, ::LargeStorageUnitGuiClient)
        GuiComponentsClient.register(ChargingSlot.TYPE, ::ChargingSlotClient)
        GuiComponentsClient.register(SuppliedShapeSelection.TYPE, ::SuppliedShapeSelectionClient)
        GuiComponentsClient.register(ToggleCheckbox.TYPE, ::ToggleCheckboxClient)

        YAIKeybinds.init(modEventBus)
        modEventBus.register(this)
        modEventBus.register(YAIModelLoaders)

        NeoForge.EVENT_BUS.addListener(::onMouseScrollEvent)

        if (ModList.get().isLoaded("curios")) {
            try {
                val clazz = Class.forName("me.luligabi.yet_another_industrialization.client.YAICurioRenderers")

                val instanceField = clazz.getField("INSTANCE")
                val instance = instanceField.get(null)

                val method = clazz.getDeclaredMethod("init", IEventBus::class.java)
                method.isAccessible = true

                method.invoke(instance, modEventBus)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @SubscribeEvent
    private fun onClientSetup(event: FMLClientSetupEvent) {
        for (blockDef in YAIBlocks.Registry.BLOCKS.entries) {
            if (blockDef.get() !is MachineBlock) continue

            val blockEntity = (blockDef.get() as MachineBlock).getBlockEntityInstance()
            val renderer = when (blockEntity) {
                is ArboreousGreenhouseBlockEntity -> BlockEntityRendererProvider { ArboreousGreenhouseBER(it) }
                is FlightPylonBlockEntity -> BlockEntityRendererProvider { FlightPylonBER(it) }
                is MultiblockMachineBlockEntity -> BlockEntityRendererProvider { MultiblockMachineBER(it) }
                is DetonationChamberCasingBlockEntity -> BlockEntityRendererProvider { DetonationChamberCasingBER(it) }
                else -> BlockEntityRendererProvider { MachineBlockEntityRenderer(it) }
            } as BlockEntityRendererProvider<BlockEntity> // I hate generics with a passion

            BlockEntityRenderers.register(blockEntity.type, renderer)
        }
    }

    @SubscribeEvent
    private fun registerClientTooltipComponents(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register(StorageSlotLockerItem.TooltipData::class.java, ::StorageSlotLockerComponent)
    }

    private fun onMouseScrollEvent(event: InputEvent.MouseScrollingEvent) {
        val player = Minecraft.getInstance().player!!
        if (player.isShiftKeyDown && player.getItemInHand(InteractionHand.MAIN_HAND).`is`(YAIItems.STORAGE_SLOT_LOCKER.get())) {
            SlotLockerChangeModePacket(player.id, player.inventory.selected).sendToServer()
            event.setCanceled(true)
        }
    }
}