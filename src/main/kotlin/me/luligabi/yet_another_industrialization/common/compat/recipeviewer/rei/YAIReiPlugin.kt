package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.rei

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import me.shedaniel.rei.forge.REIPluginClient

@REIPluginClient
class YAIReiPlugin: REIClientPlugin {

    override fun registerScreens(registry: ScreenRegistry) {
        registry.registerDraggableStackVisitor(StorageSlotLockerDraggableStackVisitor())
    }
}