package me.luligabi.yet_another_industrialization.client.component

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient
import aztech.modern_industrialization.client.machines.gui.MachineScreen
import aztech.modern_industrialization.util.Rectangle
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox
import me.luligabi.yet_another_industrialization.common.misc.network.ToggleCheckboxPacket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Checkbox

class ToggleCheckboxClient(params: ToggleCheckbox.ParamData, data: Boolean): GuiComponentClient<ToggleCheckbox.ParamData, Boolean>(params, data) {

    override fun createRenderer(screen: MachineScreen): ClientComponentRenderer {
        return object : ClientComponentRenderer {

            override fun addButtons(container: ClientComponentRenderer.ButtonContainer) {
                container.addButton(
                    -24, 73,
                    20, 20,
                    { syncId -> (ToggleCheckboxPacket(syncId, !data)).sendToServer() },
                    { params.tooltip },
                    { _, button, guiGraphics, mouseX, mouseY, _ ->

                        val isHovered = mouseX >= button.x && mouseX < button.x + 20
                                     && mouseY >= button.y && mouseY < button.y + 20
                        val sprite = if (data) {
                             if (isHovered) Checkbox.CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE else Checkbox.CHECKBOX_SELECTED_SPRITE
                        } else {
                            if (isHovered) Checkbox.CHECKBOX_HIGHLIGHTED_SPRITE else Checkbox.CHECKBOX_SPRITE
                        }
                        guiGraphics.blitSprite(sprite, button.x, button.y, 20, 20)
                    }
                )
            }

            private fun getBox(leftPos: Int, topPos: Int): Rectangle {
                return Rectangle(
                    leftPos - 23, topPos + 74,
                    31, 34
                )
            }

            override fun addExtraBoxes(rectangles: MutableList<Rectangle>, leftPos: Int, topPos: Int) {
                rectangles.add(getBox(leftPos, topPos))
            }

            override fun renderBackground(gui: GuiGraphics, leftPos: Int, topPos: Int) {
                val box = getBox(leftPos, topPos)
                gui.blit(MachineScreen.BACKGROUND, box.x() + X_OFFSET, box.y() + Y_OFFSET, 0, 0, box.w(), box.h() - 4)
                gui.blit(MachineScreen.BACKGROUND, box.x() + X_OFFSET, box.y() + Y_OFFSET + box.h() - 4, 0, 252, box.w(), 4)
            }

        }
    }

    private companion object {
        const val X_OFFSET = -8
        const val Y_OFFSET = -8
    }
}