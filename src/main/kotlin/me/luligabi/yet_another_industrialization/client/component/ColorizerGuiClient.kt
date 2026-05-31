package me.luligabi.yet_another_industrialization.client.component

import aztech.modern_industrialization.client.machines.gui.ClientComponentRenderer
import aztech.modern_industrialization.client.machines.gui.GuiComponentClient
import aztech.modern_industrialization.client.machines.gui.MachineScreen
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.network.ToggleColorizerOptionPacket
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import me.luligabi.yet_another_industrialization.common.util.toComponent
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ColorizerGuiClient(params: BooleanArray, data: BooleanArray): GuiComponentClient<BooleanArray, BooleanArray>(params, data) {

    override fun createRenderer(screen: MachineScreen) = Renderer()

    inner class Renderer(): ClientComponentRenderer {

        override fun addButtons(container: ClientComponentRenderer.ButtonContainer) {
            for (i in this@ColorizerGuiClient.data.indices) {
                container.addButton(
                    getX(i), getY(i) + 10, 20, 20,
                    { syncId -> ToggleColorizerOptionPacket(syncId, i, !data[i]).sendToServer() },
                    {
                        listOf(
                            ButtonStyles.entries[i].title.applyStyle(TextHelper.NUMBER_TEXT),
                            data[i].toComponent()
                        )
                    },
                    { _, button, gui, _, _, _ ->
                        gui.blitSprite(
                            SPRITES.get(data[i], button.isHoveredOrFocused),
                            button.x, button.y,
                            20, 20
                        )
                        gui.renderItem(ItemStack(ButtonStyles.entries[i].item), button.x + 2, button.y + 2)
                    }
                )
            }


        }

        override fun renderBackground(guiGraphics: GuiGraphics, leftPos: Int, topPos: Int) {
        }

    }

    private enum class ButtonStyles(
        val title: MutableComponent,
        val item: Item
    ) {

        WHITE(YAI.TEXT.white(), Items.WHITE_DYE),
        ORANGE(YAI.TEXT.orange(), Items.ORANGE_DYE),
        MAGENTA(YAI.TEXT.magenta(), Items.MAGENTA_DYE),
        LIGHT_BLUE(YAI.TEXT.lightBlue(), Items.LIGHT_BLUE_DYE),
        YELLOW(YAI.TEXT.yellow(), Items.YELLOW_DYE),
        LIME(YAI.TEXT.lime(), Items.LIME_DYE),
        PINK(YAI.TEXT.pink(), Items.PINK_DYE),
        GRAY(YAI.TEXT.gray(), Items.GRAY_DYE),
        LIGHT_GRAY(YAI.TEXT.lightGray(), Items.LIGHT_GRAY_DYE),
        CYAN(YAI.TEXT.cyan(), Items.CYAN_DYE),
        PURPLE(YAI.TEXT.purple(), Items.PURPLE_DYE),
        BLUE(YAI.TEXT.blue(), Items.BLUE_DYE),
        BROWN(YAI.TEXT.brown(), Items.BROWN_DYE),
        GREEN(YAI.TEXT.green(), Items.GREEN_DYE),
        RED(YAI.TEXT.red(), Items.RED_DYE),
        BLACK(YAI.TEXT.black(), Items.BLACK_DYE);

    }


    private companion object {

        // TODO get from AbstractButton
        val SPRITES = WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted")
        )

        fun getX(index: Int) = (index % 8) * 20 + 8

        fun getY(index: Int) = (index / 8) * 20 + 6
    }
    
}