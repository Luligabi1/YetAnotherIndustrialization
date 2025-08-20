package me.luligabi.yet_another_industrialization.client

import aztech.modern_industrialization.machines.GuiComponentsClient
import aztech.modern_industrialization.machines.MachineBlock
import aztech.modern_industrialization.machines.MachineBlockEntityRenderer
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import me.luligabi.yet_another_industrialization.client.renderer.ArboreousGreenhouseBER
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.chunky_tank.DummyShapeSelection
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@Mod(YAI.ID, dist = [Dist.CLIENT])
@EventBusSubscriber(value = [Dist.CLIENT], modid = YAI.ID, bus = EventBusSubscriber.Bus.MOD)
object YAIClient {

    init {
        GuiComponentsClient.register(DummyShapeSelection.ID, ::DummyShapeSelectionClient)
        GuiComponentsClient.register(ArboreousGreenhouseBlockEntity.SoilSelection.ID, ::SoilSelectionClient)
    }

    @Suppress("UNCHECKED_CAST")
    @SubscribeEvent
    private fun registerBlockEntityRenderers(event: FMLClientSetupEvent) {
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

}