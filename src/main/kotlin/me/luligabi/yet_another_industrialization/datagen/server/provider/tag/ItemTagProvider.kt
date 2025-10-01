package me.luligabi.yet_another_industrialization.datagen.server.provider.tag

import aztech.modern_industrialization.MIItem
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterials
import net.minecraft.core.HolderLookup
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import java.util.concurrent.CompletableFuture

class ItemTagProvider(event: GatherDataEvent): ItemTagsProvider(event.generator.packOutput, event.lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()), YAI.ID, event.existingFileHelper) {

    override fun addTags(lookupProvider: HolderLookup.Provider) {
        tag(YAITags.PLATES_BATTERY_ALLOY)
            .add(YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.PLATE).asItem())

        tag(YAITags.GEARS_BATTERY_ALLOY)
            .add(YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.GEAR).asItem())

        tag(YAITags.RODS_BATTERY_ALLOY)
            .add(YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.ROD).asItem())

        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS)
            .add(YAIItems.CHA_CHA_REAL_SMOOTH.get())
            .add(YAIItems.ENERGY_ZAP.get())

        tag(YAITags.MI_GUIDE_BOOKS)
            .add(MIItem.GUIDE_BOOK.asItem())
            .add(YAIItems.GUIDEBOOK.get())
            .addOptional(ResourceLocation.fromNamespaceAndPath("hostile_neural_industrialization", "guidebook"))
    }
}