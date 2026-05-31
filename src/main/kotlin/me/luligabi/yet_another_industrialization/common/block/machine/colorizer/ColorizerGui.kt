package me.luligabi.yet_another_industrialization.common.block.machine.colorizer

import aztech.modern_industrialization.machines.gui.GuiComponentServer
import io.netty.buffer.ByteBuf
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.codec.StreamCodec

class ColorizerGui(
    val component: ColorizerComponent,
    private val array: () -> BooleanArray
): GuiComponentServer<BooleanArray, BooleanArray> {

    override fun getParams() = array().copyOf()

    override fun extractData() = getParams()

    override fun getType() = TYPE

    companion object {
        val BOOLEAN_ARRAY = StreamCodec.of<ByteBuf, BooleanArray>(
            { buf, array ->
                buf.writeInt(array.size)
                val byteArray = ByteArray(array.size) { if (array[it]) 1 else 0 }
                buf.writeBytes(byteArray)
            },
            { buf ->
                val size = buf.readInt()
                val byteArray = ByteArray(size)
                buf.readBytes(byteArray)
                BooleanArray(size) { byteArray[it] == 1.toByte() }
            }
        )

        val TYPE = GuiComponentServer.Type(
            YAI.id(ColorizerBlockEntity.ID),
            BOOLEAN_ARRAY,
            BOOLEAN_ARRAY
        )
    }

}