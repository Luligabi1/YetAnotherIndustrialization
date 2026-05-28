package me.luligabi.yet_another_industrialization.client.renderer

import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import me.luligabi.yet_another_industrialization.client.YAIClient
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction

class FlightPylonBER(ctx: BlockEntityRendererProvider.Context) : MultiblockMachineBER(ctx) {

    override fun render(be: MultiblockMachineBlockEntity, tickDelta: Float, poseStack: PoseStack, vcp: MultiBufferSource, light: Int, overlay: Int) {
        super.render(be, tickDelta, poseStack, vcp, light, overlay)
        if (YAIClient.CONFIG.flightPylon().disableBeacon()) return

        val pylon = (be as FlightPylonBlockEntity)
        if (!pylon.isActive.isActive || !pylon.beaconComponent.enabled) return

        poseStack.pushPose()
        Directions.apply(pylon, poseStack)
        BeaconRenderer.renderBeaconBeam(
            poseStack, vcp,
            BeaconRenderer.BEAM_LOCATION,
            tickDelta, 1.0F, pylon.level!!.gameTime,
            9, 1024,
            pylon.getTier().beaconColor,
            0.2f, 0.25f
        )
        poseStack.popPose()
    }

    override fun shouldRenderOffScreen(pBlockEntity: MultiblockMachineBlockEntity) = true

    private enum class Directions(val xOffset: Double, val zOffset: Double) {

        NORTH(.0, 1.0),
        SOUTH(.0, -1.0),
        WEST(1.0, 0.0),
        EAST(-1.0, 0.0);

        companion object {

            fun apply(be: MachineBlockEntity, poseStack: PoseStack) {
                val direction = get(be.orientation.facingDirection)
                poseStack.translate(direction.xOffset, .0, direction.zOffset)
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