package me.luligabi.yet_another_industrialization.common.block.machine.util.components

import aztech.modern_industrialization.machines.gui.GuiComponent
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.ComponentSerialization
import java.util.stream.IntStream

/**
  * [ShapeSelection], but [lines] are supplied.
  * Used for multiblocks where the tiers are defined through data maps
  */
class SuppliedShapeSelection(
    val behavior: ShapeSelection.Behavior,
    private vararg val lines: () -> ShapeSelection.LineInfo
): GuiComponent.Server<IntArray> {

    override fun copyData(): IntArray {
        return IntStream.range(0, lines.size)
            .map { line: Int -> behavior.getCurrentIndex(line) }.toArray()
    }

    override fun needsSync(cachedData: IntArray): Boolean {
        for (i in lines.indices) {
            if (cachedData[i] != behavior.getCurrentIndex(i)) {
                return true
            }
        }
        return false
    }

    override fun writeInitialData(buf: RegistryFriendlyByteBuf) {
        buf.writeVarInt(lines.size)
        for (line in lines) {
            val currentLine = line()
            buf.writeVarInt(currentLine.numValues)
            for (component in currentLine.translations) {
                ComponentSerialization.STREAM_CODEC.encode(buf, component)
            }
            buf.writeBoolean(currentLine.useArrows)
        }
        writeCurrentData(buf)
    }

    override fun writeCurrentData(buf: RegistryFriendlyByteBuf) {
        for (i in lines.indices) {
            buf.writeVarInt(behavior.getCurrentIndex(i))
        }
    }

    override fun getId() = ID

    companion object {
        val ID = YAI.id("supplied_shape_selection")
    }

}