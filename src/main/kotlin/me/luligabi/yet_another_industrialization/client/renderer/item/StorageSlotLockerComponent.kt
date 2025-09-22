package me.luligabi.yet_another_industrialization.client.renderer.item

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.storage.TransferVariant
import aztech.modern_industrialization.util.RenderHelper
import com.mojang.blaze3d.systems.RenderSystem
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.StorageSlotLockerItem
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent

class StorageSlotLockerComponent(private val data: StorageSlotLockerItem.TooltipData): ClientTooltipComponent {

    override fun renderImage(font: Font, x: Int, y: Int, gui: GuiGraphics) {
        if (!data.itemVariant.isBlank) {
            RenderHelper.renderAndDecorateItem(
                gui, font,
                data.itemVariant.toStack(),
                x + 1, y + 1
            )
        }
        RenderSystem.disableDepthTest()
        gui.blit(TEXTURE, x, y, ITEM_X, getTextureY(data.itemVariant, true), 18, 18)
        RenderSystem.enableDepthTest()

        if (!data.fluidVariant.isBlank) {
            RenderHelper.drawFluidInGui(
                gui, data.fluidVariant,
                x + 23, y + 1
            )
        }
        gui.blit(TEXTURE, x + 22, y, FLUID_X, getTextureY(data.fluidVariant, false), 18, 18)
    }

    private fun getTextureY(variant: TransferVariant<*>, isItem: Boolean): Int {
        if (variant.isBlank) return NOT_SET_Y
        return if (isItem) checkLock(data.mode.lockItem) else checkLock(data.mode.lockFluid)
    }

    private fun checkLock(lock: Boolean?) = when (lock) {
        true -> LOCK_Y
        false -> UNLOCK_Y
        else -> NONE_Y
    }

    override fun getWidth(p0: Font) = 40

    override fun getHeight() = 22

    private companion object {

        val TEXTURE = YAI.id("textures/gui/container/slot_atlas.png")

        const val ITEM_X = 18
        const val FLUID_X = 36

        const val NOT_SET_Y = 0
        const val LOCK_Y = 18
        const val UNLOCK_Y = 36
        const val NONE_Y = 54 // no texture
    }
}