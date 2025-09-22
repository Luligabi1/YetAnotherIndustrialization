package me.luligabi.yet_another_industrialization.client.component

import aztech.modern_industrialization.inventory.BackgroundRenderedSlot
import aztech.modern_industrialization.inventory.SlotGroup
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer
import aztech.modern_industrialization.machines.gui.GuiComponent.MenuFacade
import aztech.modern_industrialization.machines.gui.GuiComponentClient
import aztech.modern_industrialization.machines.gui.MachineScreen
import aztech.modern_industrialization.util.Rectangle
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.ChargingSlot
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.swedz.tesseract.neoforge.compat.mi.tooltip.MICompatibleTextLine

class ChargingSlotClient(buf: RegistryFriendlyByteBuf): GuiComponentClient {

    override fun readCurrentData(buf: RegistryFriendlyByteBuf) {
    }

    override fun setupMenu(menu: MenuFacade) {
        class ClientSlot : SlotWithBackground(
            SimpleContainer(1), 0,
            ChargingSlot.SLOT_X, ChargingSlot.SLOT_Y
        ), TooltippedSlot {

            override fun mayPlace(stack: ItemStack) = ChargingSlot.mayPlace(stack)

            override fun getBackgroundAtlasLocation() = YAI.id("textures/gui/container/slot_atlas.png")

            override val tooltip = MICompatibleTextLine.line(YAIText.CHARGING_SLOT_TOOLTIP)
        }

        menu.addSlotToMenu(ClientSlot(), SlotGroup.CONFIGURABLE_STACKS)
    }


    override fun createRenderer(screen: MachineScreen): ClientComponentRenderer {
        return object : ClientComponentRenderer {

            private fun getBox(): Rectangle {
                return Rectangle(
                    screen.guiLeft + ChargingSlot.SLOT_X,
                    screen.guiTop + ChargingSlot.SLOT_Y,
                    29, 32
                )
            }

            override fun addExtraBoxes(rectangles: MutableList<Rectangle>, leftPos: Int, topPos: Int) {
                rectangles.add(getBox())
            }

            override fun renderBackground(gui: GuiGraphics, leftPos: Int, topPos: Int) {
                val box = getBox()
                gui.blit(MachineScreen.BACKGROUND, box.x() + X_OFFSET, box.y() + Y_OFFSET, 0, 0, box.w(), box.h() - 4)
                gui.blit(MachineScreen.BACKGROUND, box.x() + X_OFFSET, box.y() + Y_OFFSET + box.h() - 4, 0, 252, box.w(), 4)
            }

            override fun renderTooltip(screen: MachineScreen, font: Font, gui: GuiGraphics, x: Int, y: Int, cursorX: Int, cursorY: Int) {
                (screen.focusedSlot as? TooltippedSlot)?.let {
                    if (screen.focusedSlot.hasItem()) return
                    gui.renderTooltip(font, it.tooltip, cursorX, cursorY)
                }
            }



        }
    }

    private companion object {
        const val X_OFFSET = -8
        const val Y_OFFSET = -8
    }

    interface TooltippedSlot {
        val tooltip: Component
    }

    private open class SlotWithBackground(container: Container, index: Int, x: Int, y: Int) : Slot(container, index, x, y), BackgroundRenderedSlot

}