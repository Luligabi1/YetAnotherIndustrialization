package me.luligabi.yet_another_industrialization.client

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer.ButtonContainer
import aztech.modern_industrialization.machines.gui.GuiComponentClient
import aztech.modern_industrialization.machines.gui.MachineScreen
import aztech.modern_industrialization.machines.gui.MachineScreen.MachineButton
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import aztech.modern_industrialization.util.Rectangle
import aztech.modern_industrialization.util.TextHelper
import com.mojang.blaze3d.systems.RenderSystem
import me.luligabi.yet_another_industrialization.common.misc.network.SoilSelectPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import kotlin.math.max

// I love copy pasting!!!!!!
class SoilSelectionClient(buf: RegistryFriendlyByteBuf) : GuiComponentClient {

    private var lines: Array<ShapeSelection.LineInfo> = Array(buf.readVarInt()) { ShapeSelection.LineInfo(0, emptyList(), false) }
    private var currentData: IntArray
    private lateinit var renderer: Renderer

    init {
        for (i in lines.indices) {
            val numValues = buf.readVarInt()
            val components = mutableListOf<Component>()


            for (j in 0..<numValues) {
                components.add(ComponentSerialization.STREAM_CODEC.decode(buf) as Component)
            }

            lines[i] = ShapeSelection.LineInfo(numValues, components, buf.readBoolean())
        }

        currentData = IntArray(lines.size)
        readCurrentData(buf)
    }

    override fun readCurrentData(buf: RegistryFriendlyByteBuf) {
        for (i in currentData.indices) {
            currentData[i] = buf.readVarInt()
        }
    }

    override fun createRenderer(machineScreen: MachineScreen?): ClientComponentRenderer {
        var maxWidth = 1

        for (line in lines) {
            for (tooltip in line.translations()) {
                maxWidth = max(maxWidth, Minecraft.getInstance().font.width(tooltip))
            }
        }

        return Renderer(maxWidth).also { renderer = it }
    }


    inner class Renderer (private val textMaxWidth: Int) : ClientComponentRenderer {
        var isPanelOpen: Boolean = false
        private val btnSize = 12
        private val borderSize = 3
        private val outerPadding = 5
        private val innerPadding = 5
        private val panelWidth: Int

        init {
            panelWidth = 25 + textMaxWidth + 5 + 12 + 5
        }

        override fun addButtons(container: ButtonContainer) {
            for (i in this@SoilSelectionClient.lines.indices) {
                val line: ShapeSelection.LineInfo = this@SoilSelectionClient.lines[i]
                val baseU = if (line.useArrows()) 174 else 150
                val v = 58
                container.addButton(
                    -panelWidth + 3 + 5,
                    getVerticalPos(i),
                    12,
                    12,
                    { syncId -> (SoilSelectPacket(syncId, i, true)).sendToServer() },
                    { mutableListOf() },
                    { screen: MachineScreen?, button: MachineButton?, guiGraphics: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float ->
                        if (this@SoilSelectionClient.currentData[i] == 0) {
                            screen!!.blitButtonNoHighlight(button, guiGraphics, baseU, v + 12)
                        } else {
                            screen!!.blitButtonSmall(button, guiGraphics, baseU, v)
                        }
                    },
                    { isPanelOpen })
                container.addButton(
                    -17,
                    getVerticalPos(i),
                    12,
                    12,
                    { syncId -> (SoilSelectPacket(syncId, i, false)).sendToServer() },
                    { mutableListOf() },
                    { screen: MachineScreen?, button: MachineButton?, guiGraphics: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float ->
                        if (this@SoilSelectionClient.currentData[i] == line.numValues() - 1) {
                            screen!!.blitButtonNoHighlight(button, guiGraphics, baseU + 12, v + 12)
                        } else {
                            screen!!.blitButtonSmall(button, guiGraphics, baseU + 12, v)
                        }
                    },
                    { isPanelOpen })
            }

            container.addButton(
                -24,
                17,
                20,
                20,
                { _ -> isPanelOpen = !isPanelOpen },
                {
                    listOf(
                        MIText.ShapeSelectionTitle.text(),
                        MIText.ShapeSelectionDescription.text().setStyle(TextHelper.GRAY_TEXT)
                    )
                },
                { screen: MachineScreen?, button: MachineButton?, guiGraphics: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float ->
                    screen!!.blitButton(
                        button,
                        guiGraphics,
                        138,
                        38
                    )
                })
        }

        override fun renderBackground(guiGraphics: GuiGraphics, leftPos: Int, topPos: Int) {
            val box = getBox(leftPos, topPos)
            guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y(), 0, 0, box.w(), box.h() - 4)
            guiGraphics.blit(MachineScreen.BACKGROUND, box.x(), box.y() + box.h() - 4, 0, 252, box.w(), 4)
            if (isPanelOpen) {
                RenderSystem.disableDepthTest()

                for (i in this@SoilSelectionClient.lines.indices) {
                    val line: ShapeSelection.LineInfo = this@SoilSelectionClient.lines[i]
                    val tooltip = line.translations().get(this@SoilSelectionClient.currentData[i]) as Component
                    val width = Minecraft.getInstance().font.width(tooltip)
                    guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        tooltip,
                        box.x() + 3 + 5 + 12 + 5 + (textMaxWidth - width) / 2,
                        topPos + getVerticalPos(i) + 2,
                        4210752,
                        false
                    )
                }

                RenderSystem.enableDepthTest()
            }
        }

        fun getBox(leftPos: Int, topPos: Int): Rectangle {
            if (isPanelOpen) {
                val topOffset = 10
                return Rectangle(
                    leftPos - panelWidth,
                    topPos + topOffset,
                    panelWidth,
                    getVerticalPos(this@SoilSelectionClient.lines.size - 1) - topOffset + 12 + 5 + 3
                )
            } else {
                return Rectangle(leftPos - 31, topPos + 10, 31, 34)
            }
        }

        override fun addExtraBoxes(rectangles: MutableList<Rectangle?>, leftPos: Int, topPos: Int) {
            rectangles.add(getBox(leftPos, topPos))
        }

        private fun getVerticalPos(lineId: Int): Int {
            return 46 + 16 * lineId
        }
    }
}