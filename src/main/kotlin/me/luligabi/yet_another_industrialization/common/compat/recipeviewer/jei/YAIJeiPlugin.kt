package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.jei

import me.luligabi.yet_another_industrialization.common.YAI
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

@JeiPlugin
class YAIJeiPlugin: IModPlugin {

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addGhostIngredientHandler(AbstractContainerScreen::class.java, StorageSlotLockerGhostIngredientHandler())
    }

    override fun getPluginUid() = YAI.id("jei_plugin")
}