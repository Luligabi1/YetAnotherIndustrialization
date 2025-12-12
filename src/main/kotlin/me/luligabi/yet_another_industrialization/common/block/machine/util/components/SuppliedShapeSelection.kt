package me.luligabi.yet_another_industrialization.common.block.machine.util.components

import aztech.modern_industrialization.machines.gui.GuiComponentServer
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.codec.ByteBufCodecs
import java.util.stream.IntStream

/**
  * [ShapeSelection], but [lines] are supplied.
  * Used for multiblocks where the tiers are defined through data maps
  */
class SuppliedShapeSelection(
    val behavior: ShapeSelection.Behavior,
    private vararg val lines: () -> ShapeSelection.LineInfo
): GuiComponentServer<List<ShapeSelection.LineInfo>, List<Int>> {

    override fun getParams(): List<ShapeSelection.LineInfo> {
        return lines.map { it() }
    }

    override fun extractData(): List<Int> {
        return IntStream.range(0, lines.size).map(behavior::getCurrentIndex).boxed().toList()
    }

    override fun getType() = TYPE

    companion object {
        val TYPE = GuiComponentServer.Type(
            YAI.id("supplied_shape_selection"),
            ShapeSelection.LineInfo.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list())
        )
    }

}