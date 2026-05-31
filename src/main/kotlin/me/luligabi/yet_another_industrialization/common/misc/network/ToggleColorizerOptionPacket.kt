package me.luligabi.yet_another_industrialization.common.misc.network

import aztech.modern_industrialization.machines.gui.MachineMenuServer
import aztech.modern_industrialization.network.MIStreamCodecs
import me.luligabi.yet_another_industrialization.common.block.machine.colorizer.ColorizerGui
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class ToggleColorizerOptionPacket(
    private val syncId: Int,
    private val index: Int,
    private val toggle: Boolean
): CustomPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE, ToggleColorizerOptionPacket::syncId,
            ByteBufCodecs.VAR_INT, ToggleColorizerOptionPacket::index,
            ByteBufCodecs.BOOL, ToggleColorizerOptionPacket::toggle,
            ::ToggleColorizerOptionPacket
        )
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        val menu = ctx.player.containerMenu
        if (menu.containerId == syncId && menu is MachineMenuServer) {
            val colorizer = menu.blockEntity.guiComponents.getOrThrow(ColorizerGui::class.java)
            colorizer.component.setColor(index, toggle)
        }
    }

    override fun type() = YAIPackets.getType(this::class.java)
}