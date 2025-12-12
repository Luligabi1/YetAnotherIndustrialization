package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.machines.gui.GuiComponentServer
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitGui.Data
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

class LargeStorageUnitGui(
    private val isShapeValid: () -> Boolean,
    private val euSupplier: () -> Long,
    private val maxEuSupplier: () -> Long
) : GuiComponentServer<Data, Data> {

    override fun getParams() = Data(isShapeValid(), euSupplier(), maxEuSupplier())

    override fun extractData() = getParams()

    override fun getType() = TYPE

    companion object {
        val TYPE = GuiComponentServer.Type(
            YAI.id(LargeStorageUnitBlockEntity.ID),
            Data.STREAM_CODEC,
            Data.STREAM_CODEC
        )
    }

    data class Data(val isShapeValid: Boolean, val eu: Long, val maxEu: Long) {
        companion object {
            val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Data::isShapeValid,
                ByteBufCodecs.VAR_LONG, Data::eu,
                ByteBufCodecs.VAR_LONG, Data::maxEu,
                ::Data
            )
        }
    }

}