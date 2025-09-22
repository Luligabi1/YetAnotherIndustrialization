package me.luligabi.yet_another_industrialization.common.misc.network

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.swedz.tesseract.neoforge.packet.CustomPacket
import net.swedz.tesseract.neoforge.packet.PacketRegistry

object YAIPackets {

    fun init(event: RegisterPayloadHandlersEvent) {
        create("supplied_shape_select", SuppliedShapeSelect::class.java, SuppliedShapeSelect.STREAM_CODEC)
        create("slot_locker_change_mode", SlotLockerChangeModePacket::class.java, SlotLockerChangeModePacket.STREAM_CODEC)
        create("slot_locker_drag_packet", SlotLockerDragPacket::class.java, SlotLockerDragPacket.STREAM_CODEC)

        REGISTRY.registerAll(event)
    }

    fun getType(packetClass: Class<out CustomPacket>) = REGISTRY.getType(packetClass)

    private fun <P : CustomPacket> create(
        id: String,
        packetClass: Class<P>,
        packetCodec: StreamCodec<in RegistryFriendlyByteBuf, P>
    ) {
        REGISTRY.create(id, packetClass, packetCodec)
    }

    private val REGISTRY = PacketRegistry.create<CustomPacket>(YAI.ID)
}