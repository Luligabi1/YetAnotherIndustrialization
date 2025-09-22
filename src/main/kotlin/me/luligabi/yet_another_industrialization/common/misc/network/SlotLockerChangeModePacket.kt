package me.luligabi.yet_another_industrialization.common.misc.network

import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIDataComponents
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class SlotLockerChangeModePacket(
    private val playerId: Int,
    private val slotIndex: Int
): CustomPacket {

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SlotLockerChangeModePacket::playerId,
            ByteBufCodecs.VAR_INT, SlotLockerChangeModePacket::slotIndex,
            ::SlotLockerChangeModePacket
        )
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        ctx.player.inventory.getItem(slotIndex).let {
            if (!it.`is`(YAIItems.STORAGE_SLOT_LOCKER.get())) return

            val oldData = it.get(YAIDataComponents.SLOT_LOCKER_DATA) ?: return

            val mode = oldData.mode.next()
            val newData = oldData.copy(mode = mode)
            it.set(YAIDataComponents.SLOT_LOCKER_DATA, newData)

            ctx.player.displayClientMessage(
                YAIText.SLOT_LOCKER_MODE_CHANGE.text(
                    mode.text.text().applyStyle(TextHelper.NUMBER_TEXT)
                ).applyStyle(TextHelper.GRAY_TEXT),
                true
            )
        }
    }

    override fun type() = YAIPackets.getType(this::class.java)
}