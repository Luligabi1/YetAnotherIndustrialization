package me.luligabi.yet_another_industrialization.client.renderer

import aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class DetonationChamberCasingBER(ctx: BlockEntityRendererProvider.Context): MachineBlockEntityRenderer<DetonationChamberCasingBlockEntity>(ctx) {

    override fun render(be: DetonationChamberCasingBlockEntity, tickDelta: Float, poseStack: PoseStack, vcp: MultiBufferSource, light: Int, overlay: Int) {
        super.render(be, tickDelta, poseStack, vcp, light, overlay)

        val progress: Float = (be as DetonationChamberCasingBlockEntity).data.progress

        val red = 1f
        val green = 1f - progress
        val blue = 1f - progress
        val alpha = 0.3f + (progress * 0.5f)

        poseStack.pushPose()


        val buffer = vcp.getBuffer(RenderType.translucent())
        val pose = poseStack.last()


        renderColoredCube(pose, buffer, red, green, blue, alpha, light, overlay)

        poseStack.popPose()
    }

    private fun renderColoredCube(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        light: Int,
        overlay: Int
    ) {
        val size = 0.001f
        val min = -size
        val max = 1f + size


        addVertex(pose, buffer, min, min, max, 0f, 0f, 1f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, min, max, 0f, 0f, 1f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, max, max, 0f, 0f, 1f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, max, max, 0f, 0f, 1f, red, green, blue, alpha, light, overlay)


        addVertex(pose, buffer, min, min, min, 0f, 0f, -1f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, max, min, 0f, 0f, -1f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, max, min, 0f, 0f, -1f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, min, min, 0f, 0f, -1f, red, green, blue, alpha, light, overlay)


        addVertex(pose, buffer, max, min, min, 1f, 0f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, max, min, 1f, 0f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, max, max, 1f, 0f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, min, max, 1f, 0f, 0f, red, green, blue, alpha, light, overlay)


        addVertex(pose, buffer, min, min, min, -1f, 0f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, min, max, -1f, 0f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, max, max, -1f, 0f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, max, min, -1f, 0f, 0f, red, green, blue, alpha, light, overlay)


        addVertex(pose, buffer, min, max, min, 0f, 1f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, max, max, 0f, 1f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, max, max, 0f, 1f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, max, min, 0f, 1f, 0f, red, green, blue, alpha, light, overlay)


        addVertex(pose, buffer, min, min, min, 0f, -1f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, min, min, 0f, -1f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, max, min, max, 0f, -1f, 0f, red, green, blue, alpha, light, overlay)
        addVertex(pose, buffer, min, min, max, 0f, -1f, 0f, red, green, blue, alpha, light, overlay)
    }

    private fun addVertex(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        x: Float, y: Float, z: Float,
        nx: Float, ny: Float, nz: Float,
        red: Float, green: Float, blue: Float, alpha: Float,
        light: Int,
        overlay: Int
    ) {
        buffer.addVertex(pose.pose(), x, y, z)
            .setColor(red, green, blue, alpha)
            .setUv(0f, 0f)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, nx, ny, nz)
    }

    override fun shouldRenderOffScreen(pBlockEntity: DetonationChamberCasingBlockEntity) = true
}