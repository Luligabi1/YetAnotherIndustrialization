package me.luligabi.hostile_neural_industrialization.common.misc.network

import aztech.modern_industrialization.machines.gui.MachineMenuServer
import aztech.modern_industrialization.network.MIStreamCodecs
import me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class SoilSelectPacket(
    private val syncId: Int,
    private val shapeLine: Int,
    private val clickedLeftButton: Boolean
): CustomPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE, SoilSelectPacket::syncId,
            ByteBufCodecs.VAR_INT, SoilSelectPacket::shapeLine,
            ByteBufCodecs.BOOL, SoilSelectPacket::clickedLeftButton,
            ::SoilSelectPacket
        )
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        val menu = ctx.player.containerMenu
        if (menu.containerId == syncId && menu is MachineMenuServer) {
            val shapeSelection = menu.blockEntity.guiComponents.get<ArboreousGreenhouseBlockEntity.SoilSelection>(ArboreousGreenhouseBlockEntity.SoilSelection.ID)
            shapeSelection.behavior.handleClick(shapeLine, if (clickedLeftButton) -1 else +1)
        }
    }

    override fun type() = HNIPackets.getType(this::class.java)
}