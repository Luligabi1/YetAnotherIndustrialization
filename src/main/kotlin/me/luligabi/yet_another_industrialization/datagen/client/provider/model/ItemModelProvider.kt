package me.luligabi.yet_another_industrialization.datagen.client.provider.model

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


class ItemModelProvider(event: GatherDataEvent): ItemModelProvider(event.generator.packOutput, YAI.ID, event.existingFileHelper) {

    override fun registerModels() {

        for (item in YAIItems.values()) {
            if (!item.hasModelProvider()) continue
            item.modelProvider().accept(this)
        }

    }

}