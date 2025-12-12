package me.luligabi.yet_another_industrialization.client.component

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.MITooltips
import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient
import aztech.modern_industrialization.client.machines.gui.MachineScreen
import aztech.modern_industrialization.client.util.RenderHelper
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import java.util.*


class LargeStorageUnitGuiClient(params: LargeStorageUnitGui.Data, data: LargeStorageUnitGui.Data): GuiComponentClient<LargeStorageUnitGui.Data, LargeStorageUnitGui.Data>(params, data) {

    private companion object {
        val TEXTURE: ResourceLocation = YAI.id("textures/gui/container/large_storage_unit_gui.png")

        const val X = 5
        const val Y = 16
        const val WIDTH = 166
        const val HEIGHT = 43
    }

    override fun createRenderer(screen: MachineScreen) = Renderer()

    inner class Renderer: ClientComponentRenderer {

        override fun renderBackground(gui: GuiGraphics, x: Int, y: Int) {
            val minecraftClient = Minecraft.getInstance()
            gui.blit(TEXTURE, x + X, y + Y, 0f, 0f, WIDTH, HEIGHT, WIDTH, HEIGHT)
            val font = minecraftClient.font
            var deltaY = 23

            gui.drawString(
                font,
                (if (data.isShapeValid) MIText.MultiblockStatusActive else MIText.MultiblockShapeInvalid).text(),
                x + 10, y + deltaY,
                if (data.isShapeValid) 0xFFFFFF else 0xFF0000, false
            )
            deltaY += 11

            val maxedAmount = TextHelper.getMaxedAmount(data.eu, data.maxEu)
            gui.drawString(
                font,
                MIText.EuMaxed.text(maxedAmount.digit, maxedAmount.maxDigit, maxedAmount.unit),
                x + 10, y + deltaY,
                0xFFFFFF, false
            )
            deltaY += 11

            gui.drawString(
                font,
                MITooltips.RATIO_PERCENTAGE_PARSER.parse(data.eu.toDouble() / data.maxEu),
                x + 10, y + deltaY,
                0xFFFFFF, false
            )
        }

        override fun renderTooltip(screen: MachineScreen, font: Font, guiGraphics: GuiGraphics, x: Int, y: Int, cursorX: Int, cursorY: Int) {
            if (RenderHelper.isPointWithinRectangle(X, Y, WIDTH, HEIGHT, (cursorX - x).toDouble(), (cursorY - y).toDouble())) {
                val tooltip = MIText.EuMaxed.text(data.eu, data.maxEu, "")
                guiGraphics.renderTooltip(font, listOf(tooltip), Optional.empty(), cursorX, cursorY)
            }
        }

    }

}