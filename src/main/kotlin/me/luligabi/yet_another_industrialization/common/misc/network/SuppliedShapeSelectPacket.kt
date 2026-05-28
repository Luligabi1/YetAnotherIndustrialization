package me.luligabi.yet_another_industrialization.common.misc.network

import aztech.modern_industrialization.machines.gui.MachineMenuServer
import aztech.modern_industrialization.network.MIStreamCodecs
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class SuppliedShapeSelectPacket(
    private val syncId: Int,
    private val shapeLine: Int,
    private val clickedLeftButton: Boolean
): CustomPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE, SuppliedShapeSelectPacket::syncId,
            ByteBufCodecs.VAR_INT, SuppliedShapeSelectPacket::shapeLine,
            ByteBufCodecs.BOOL, SuppliedShapeSelectPacket::clickedLeftButton,
            ::SuppliedShapeSelectPacket
        )
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        val menu = ctx.player.containerMenu
        if (menu.containerId == syncId && menu is MachineMenuServer) {
            val shapeSelection = menu.blockEntity.guiComponents.getOrThrow(SuppliedShapeSelection::class.java)
            shapeSelection.behavior.handleClick(shapeLine, if (clickedLeftButton) -1 else +1)
        }
    }

    override fun type() = YAIPackets.getType(this::class.java)
}