package me.luligabi.yet_another_industrialization.client.component

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient
import aztech.modern_industrialization.client.machines.gui.MachineScreen
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import aztech.modern_industrialization.util.Rectangle
import aztech.modern_industrialization.util.TextHelper
import com.mojang.blaze3d.systems.RenderSystem
import me.luligabi.yet_another_industrialization.common.misc.network.SuppliedShapeSelect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import kotlin.math.max

// I love copy pasting!!!!!!
class SuppliedShapeSelectionClient(
    params: List<ShapeSelection.LineInfo>, 
    data: List<Int>
) : GuiComponentClient<List<ShapeSelection.LineInfo>, List<Int>>(params, data) {
    
    override fun createRenderer(machineScreen: MachineScreen): ClientComponentRenderer {
        var maxWidth = 1

        for (line in params) {
            for (tooltip in line.translations()) {
                maxWidth = max(maxWidth, Minecraft.getInstance().font.width(tooltip))
            }
        }

        return Renderer(maxWidth)
    }


    inner class Renderer (private val textMaxWidth: Int) : ClientComponentRenderer {
        var isPanelOpen: Boolean = false
        private val panelWidth: Int

        init {
            panelWidth = 25 + textMaxWidth + 5 + 12 + 5
        }

        override fun addButtons(container: ClientComponentRenderer.ButtonContainer) {
            for (i in this@SuppliedShapeSelectionClient.params.indices) {
                val line: ShapeSelection.LineInfo = this@SuppliedShapeSelectionClient.params[i]
                val baseU = if (line.useArrows()) 174 else 150
                val v = 58
                container.addButton(
                    -panelWidth + 3 + 5,
                    getVerticalPos(i),
                    12,
                    12,
                    { syncId -> (SuppliedShapeSelect(syncId, i, true)).sendToServer() },
                    { mutableListOf() },
                    { screen, button, guiGraphics, mouseX, mouseY, delta ->
                        if (this@SuppliedShapeSelectionClient.data[i] == 0) {
                            screen.blitButtonNoHighlight(button, guiGraphics, baseU, v + 12)
                        } else {
                            screen.blitButtonSmall(button, guiGraphics, baseU, v)
                        }
                    },
                    { isPanelOpen })
                container.addButton(
                    -17,
                    getVerticalPos(i),
                    12,
                    12,
                    { syncId -> (SuppliedShapeSelect(syncId, i, false)).sendToServer() },
                    { mutableListOf() },
                    { screen, button, guiGraphics, mouseX, mouseY, delta ->
                        if (this@SuppliedShapeSelectionClient.data[i] == line.numValues() - 1) {
                            screen.blitButtonNoHighlight(button, guiGraphics, baseU + 12, v + 12)
                        } else {
                            screen.blitButtonSmall(button, guiGraphics, baseU + 12, v)
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
                { screen, button, guiGraphics, mouseX: Int, mouseY: Int, delta: Float ->
                    screen.blitButton(
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

                for (i in this@SuppliedShapeSelectionClient.params.indices) {
                    val line: ShapeSelection.LineInfo = this@SuppliedShapeSelectionClient.params[i]
                    val tooltip = line.translations().get(this@SuppliedShapeSelectionClient.data[i]) as Component
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
                    getVerticalPos(this@SuppliedShapeSelectionClient.params.size - 1) - topOffset + 12 + 5 + 3
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