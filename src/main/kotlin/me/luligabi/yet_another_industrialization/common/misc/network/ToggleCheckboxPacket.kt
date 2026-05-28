package me.luligabi.yet_another_industrialization.common.misc.network

import aztech.modern_industrialization.machines.gui.MachineMenuServer
import aztech.modern_industrialization.network.MIStreamCodecs
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class ToggleCheckboxPacket(
    private val syncId: Int,
    private val value: Boolean
): CustomPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE, ToggleCheckboxPacket::syncId,
            ByteBufCodecs.BOOL, ToggleCheckboxPacket::value,
            ::ToggleCheckboxPacket
        )
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        val menu = ctx.player.containerMenu
        if (menu.containerId == syncId && menu is MachineMenuServer) {
            val shapeSelection = menu.blockEntity.guiComponents.getOrThrow(ToggleCheckbox::class.java)
            shapeSelection.action(value)
        }
    }

    override fun type() = YAIPackets.getType(this::class.java)
}