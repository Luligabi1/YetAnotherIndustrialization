package me.luligabi.yet_another_industrialization.client

import aztech.modern_industrialization.machines.GuiComponentsClient
import aztech.modern_industrialization.machines.MachineBlock
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import me.luligabi.yet_another_industrialization.client.renderer.ArboreousGreenhouseBER
import me.luligabi.yet_another_industrialization.client.renderer.item.StorageSlotLockerComponent
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.ChargingSlot
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitGui
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection
import me.luligabi.yet_another_industrialization.common.item.StorageSlotLockerItem
import me.luligabi.yet_another_industrialization.common.item.YAIItems
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
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.common.NeoForge


@Mod(YAI.ID, dist = [Dist.CLIENT])
class YAIClient(modEventBus: IEventBus, container: ModContainer) {

    init {
        GuiComponentsClient.register(DummyShapeSelection.ID, ::DummyShapeSelectionClient)
        GuiComponentsClient.register(ArboreousGreenhouseBlockEntity.SoilSelection.ID, ::SoilSelectionClient)
        NeoForge.EVENT_BUS.addListener(::onMouseScrollEvent)
    }

    @Suppress("UNCHECKED_CAST")
    @SubscribeEvent
    private fun onClientSetup(event: FMLClientSetupEvent) {
        for (blockDef in YAIBlocks.Registry.BLOCKS.entries) {
            if (blockDef.get() !is MachineBlock) continue

            val blockEntity = (blockDef.get() as MachineBlock).getBlockEntityInstance()
            val renderer = when (blockEntity) {
                is ArboreousGreenhouseBlockEntity -> BlockEntityRendererProvider { ArboreousGreenhouseBER(it) }
                is MultiblockMachineBlockEntity -> BlockEntityRendererProvider { MultiblockMachineBER(it) }
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