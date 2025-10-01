package me.luligabi.yet_another_industrialization.client.renderer

import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBER
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexFormat
import me.luligabi.yet_another_industrialization.client.model.YAIModelLoaders
import me.luligabi.yet_another_industrialization.client.model.multiblock.MultiBlockFakeLevel
import me.luligabi.yet_another_industrialization.client.model.multiblock.MultiBlockModel
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.LightLayer
import org.joml.Matrix4f
import kotlin.math.max

class ArboreousGreenhouseBER(ctx: BlockEntityRendererProvider.Context) : MultiblockMachineBER(ctx) {

    override fun render(be: MultiblockMachineBlockEntity, tickDelta: Float, poseStack: PoseStack, vcp: MultiBufferSource, light: Int, overlay: Int) {
        super.render(be, tickDelta, poseStack, vcp, light, overlay)

        val saplingData = (be as ArboreousGreenhouseBlockEntity).sapling.item?.builtInRegistryHolder()?.getData(YAIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING) ?: return
        val saplingId = saplingData.model

        if (YAIModelLoaders.MODEL_MAP.containsKey(saplingId)) {
            val model = YAIModelLoaders.MODEL_MAP[saplingId]!!

            (Minecraft.getInstance().modelManager.getModel(model) as? MultiBlockModel)?.let {
                val level = MultiBlockFakeLevel(it, be.level!!, be.blockPos)
                val vertexBuffer = YAIModelLoaders.getVbo(saplingId, it, level, be.blockPos)
                if (vertexBuffer != null && !vertexBuffer.isInvalid) {
                    vertexBuffer.bind()
                    poseStack.pushPose()

                    Directions.apply(be, poseStack)
                    poseStack.scale(4f, 4f, 4f)
                    val modelMatrix = poseStack.last().pose()

                    // Combine the camera transformation with the poseStack transformation
                    val viewMatrix = Matrix4f(RenderSystem.getModelViewMatrix())
                    viewMatrix.mul(modelMatrix)

                    YAIModelLoaders.RENDER_TYPE.setupRenderState()

                    val shader = RenderSystem.getShader()!!
                    shader.setDefaultUniforms(
                        VertexFormat.Mode.QUADS, viewMatrix,
                        RenderSystem.getProjectionMatrix(), Minecraft.getInstance().window
                    )

                    val sunBrightness = 1 - (be.level as ClientLevel).getStarBrightness(tickDelta) // Render brightness of the current world

                    val skyBrightness = be.level!!.getBrightness(LightLayer.SKY, be.blockPos.above()) / 15.0f
                    val blockBrightness = be.level!!.getBrightness(LightLayer.BLOCK, be.blockPos.above()) / 15.0f

                    // The light level that the block would receive from the sun
                    // This skylight is the light level that the block receives from the sky, decreasing e.g. when under a roof
                    // Multiply the sun brightness with the sky brightness to get the total brightness from the sun
                    val sunReceivedLight = sunBrightness * skyBrightness

                    // If the light received from other blocks is brighter than the sun, use that instead
                    val brighterBrightness = max(sunReceivedLight, blockBrightness)

                    // Clamp the brightness between 0.1 and 0.9 to avoid too dark or too bright blocks
                    val lightLevel = Math.clamp(brighterBrightness, 0.1f, 0.9f)

                    // Render the block with the calculated light level
                    RenderSystem.setShaderColor(lightLevel, lightLevel, lightLevel, 1.0f)

                    RenderSystem.setupShaderLights(shader)
                    RenderSystem.enableDepthTest()
                    RenderSystem.enableBlend()

                    //RenderSystem.depthMask(true)
                    val projectionMatrix = RenderSystem.getProjectionMatrix()
                    vertexBuffer.drawWithShader(viewMatrix, projectionMatrix, shader)

                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
                    VertexBuffer.unbind()
                    poseStack.popPose()
                }
            }
        }

    }

    override fun shouldRenderOffScreen(pBlockEntity: MultiblockMachineBlockEntity) = true

    private enum class Directions(val xOffset: Double, val zOffset: Double) {

        NORTH(0.505, 4.5),
        SOUTH(0.505, -4.5),
        WEST(4.5, 0.505),
        EAST(-4.5, 0.505);

        companion object {

            fun apply(be: MachineBlockEntity, poseStack: PoseStack) {
                val direction = get(be.orientation.facingDirection)
                poseStack.translate(direction.xOffset, 1.0, direction.zOffset)
            }

            fun get(direction: Direction) = when (direction) {
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.WEST -> WEST
                Direction.EAST -> EAST
                else -> NORTH
            }

        }

    }
}