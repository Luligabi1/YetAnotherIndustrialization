package me.luligabi.yet_another_industrialization.common.misc.network

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.item.tools.IndustrialistsGogglesItem
import me.luligabi.yet_another_industrialization.common.util.toComponent
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class ToggleIndustrialistsGogglesPacket(
    private val newState: Boolean
): CustomPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToggleIndustrialistsGogglesPacket::newState,
            ::ToggleIndustrialistsGogglesPacket
        )
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        // if the player has 2 goggles it will always toggle only one of them... does this matter? ehhhh...
        IndustrialistsGogglesItem.toggleMode(ctx.player)
        ctx.player.displayClientMessage(YAI.TEXT.enabledPrefixAlt(
            YAIItems.INDUSTRIALISTS_GOGGLES.get().defaultInstance.hoverName,
            newState.toComponent()
        ), true)
    }

    override fun type() = YAIPackets.getType(this::class.java)
}