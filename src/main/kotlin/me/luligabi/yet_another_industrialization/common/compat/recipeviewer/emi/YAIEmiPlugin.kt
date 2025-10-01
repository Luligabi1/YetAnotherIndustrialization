package me.luligabi.yet_another_industrialization.common.compat.recipeviewer.emi

import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry

@EmiEntrypoint
class YAIEmiPlugin: EmiPlugin {

    override fun register(registry: EmiRegistry) {
        registry.addGenericDragDropHandler(StorageSlotLockerDragDropHandler())
    }
}