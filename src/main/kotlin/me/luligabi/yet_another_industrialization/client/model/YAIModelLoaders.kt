package me.luligabi.yet_another_industrialization.client.model

import com.mojang.blaze3d.vertex.*
import me.luligabi.yet_another_industrialization.client.model.multiblock.MultiBlockFakeLevel
import me.luligabi.yet_another_industrialization.client.model.multiblock.MultiBlockModel
import me.luligabi.yet_another_industrialization.client.model.multiblock.MultiBlockModelLoader
import me.luligabi.yet_another_industrialization.client.model.multiblock.MultiBlockRenderer
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.model.data.ModelData

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
object YAIModelLoaders {

    val MULTIBLOCK_VBOS = mutableMapOf<ResourceLocation, VertexBuffer>()
    val MODEL_MAP = mutableMapOf<ResourceLocation, ModelResourceLocation>()

    val RENDER_TYPE = RenderType.cutout()

    fun getVbo(
        modelId: ResourceLocation,
        multiBlockModel: MultiBlockModel,
        level: MultiBlockFakeLevel,
        pos: BlockPos
    ): VertexBuffer? {
        if (!MULTIBLOCK_VBOS.containsKey(modelId)) {
            val byteBufferBuilder = ByteBufferBuilder(RENDER_TYPE.bufferSize)
            val bufferBuilder = BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK)
            val renderer = MultiBlockRenderer(Minecraft.getInstance().blockColors, level, pos, true)

            val tempPoseStack = PoseStack()
            renderer.tesselateWithAO(
                level,
                multiBlockModel, Blocks.GLASS.defaultBlockState(), pos,
                tempPoseStack, bufferBuilder, false,
                RandomSource.create(), 0,
                0xF000F0, ModelData.EMPTY, RENDER_TYPE
            )

            bufferBuilder.build()?.let {
                val modelVertexBuffer = VertexBuffer(VertexBuffer.Usage.DYNAMIC)
                modelVertexBuffer.bind()
                modelVertexBuffer.upload(it)
                VertexBuffer.unbind()
                MULTIBLOCK_VBOS.put(modelId, modelVertexBuffer)
            }
        }

        return MULTIBLOCK_VBOS[modelId]
    }

    @SubscribeEvent
    fun registerGeometryLoaders(event: ModelEvent.RegisterGeometryLoaders) {
        event.register(MultiBlockModelLoader.ID, MultiBlockModelLoader.INSTANCE)
    }

    @SubscribeEvent
    fun registerAdditional(event: ModelEvent.RegisterAdditional) {
        val treeModels = Minecraft.getInstance().resourceManager.listResources("models/multiblock") { it.path.endsWith(".json") }.keys
        for (treeModel in treeModels) {
            val modelId = ResourceLocation.fromNamespaceAndPath(
                treeModel.namespace,
                treeModel.path.replace(".json", "").replace("models/", "")
            )
            val itemParts = modelId.path.replace("multiblock/${ArboreousGreenhouseBlockEntity.ID}/", "").split("/".toRegex(), limit = 2).toTypedArray()
            if (itemParts.size != 2) {
                continue
            }

            val itemId = ResourceLocation.fromNamespaceAndPath(itemParts[0], itemParts[1])
            val modelResourceId = ModelResourceLocation.standalone(modelId)

            event.register(modelResourceId)
            MODEL_MAP.put(itemId, modelResourceId)
        }
    }

}