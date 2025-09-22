package me.luligabi.yet_another_industrialization.common.misc.network

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import me.luligabi.yet_another_industrialization.common.util.YAIDraggable
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketContext

class SlotLockerDragPacket(
    private val containerId: Int,
    private val slotIndex: Int,
    private val isItem: Boolean,
    private val itemVariant: ItemVariant?,
    private val fluidVariant: FluidVariant?
): CustomPacket {

    constructor(
        containerId: Int,
        slotIndex: Int,
        itemVariant: ItemVariant
    ) : this(containerId, slotIndex, true, itemVariant, null)

    constructor(
        containerId: Int,
        slotIndex: Int,
        fluidVariant: FluidVariant
    ) : this(containerId, slotIndex, false, null, fluidVariant)

    private constructor(
        buf: RegistryFriendlyByteBuf,
    ) : this(buf.readInt(), buf.readVarInt(), buf.readBoolean(), buf)

    private constructor(
        containerId: Int,
        slotIndex: Int,
        isItem: Boolean,
        buf: RegistryFriendlyByteBuf) :
        this(
            containerId,
            slotIndex,
            isItem,
            if (isItem) ItemVariant.fromPacket(buf) else null,
            if (isItem) null else FluidVariant.fromPacket(buf)
        )


    companion object {

        val STREAM_CODEC = StreamCodec.ofMember(
            { obj, buf -> obj.write(buf) },
            ::SlotLockerDragPacket
        )

    }
    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(containerId)
        buf.writeVarInt(slotIndex)
        buf.writeBoolean(isItem)
        if (isItem) {
            itemVariant!!.toPacket(buf)
        } else {
            fluidVariant!!.toPacket(buf)
        }
    }

    override fun handle(ctx: PacketContext) {
        ctx.assertServerbound()

        val sh = ctx.player.containerMenu
        if (sh.containerId == containerId) {
            val stack = sh.getSlot(slotIndex).item
            (stack.item as? YAIDraggable)?.let {
                if (isItem) {
                    it.dragItem(stack, itemVariant!!, false)
                } else {
                    it.dragFluid(stack, fluidVariant!!, false)
                }
            }
        }
    }

    override fun type() = YAIPackets.getType(this::class.java)
}