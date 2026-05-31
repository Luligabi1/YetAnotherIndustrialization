package me.luligabi.yet_another_industrialization.datagen.server.provider.tag

import aztech.modern_industrialization.MIItem
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.swedz.tesseract.neoforge.compat.mi.material.MIMaterials
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import java.util.concurrent.CompletableFuture

class ItemTagProvider(event: GatherDataEvent): ItemTagsProvider(event.generator.packOutput, event.lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()), YAI.ID, event.existingFileHelper) {

    override fun addTags(lookupProvider: HolderLookup.Provider) {
        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS)
            .add(YAIItems.ENERGY_ZAP.get())

        tag(YAITags.MI_GUIDE_BOOKS)
            .add(MIItem.GUIDE_BOOK.asItem())
            .add(YAIItems.GUIDEBOOK.get())
            .addOptional(ResourceLocation.fromNamespaceAndPath("hostile_neural_industrialization", "guidebook"))

        tag(YAITags.MI_FUEL_RODS_URANIUM)
            .add(MIMaterials.URANIUM.get(MIMaterialParts.FUEL_ROD).asItem())
            .add(MIMaterials.URANIUM.get(MIMaterialParts.FUEL_ROD_DOUBLE).asItem())
            .add(MIMaterials.URANIUM.get(MIMaterialParts.FUEL_ROD_QUAD).asItem())

        tag(YAITags.MI_FUEL_RODS_LE_URANIUM)
            .add(MIMaterials.LE_URANIUM.get(MIMaterialParts.FUEL_ROD).asItem())
            .add(MIMaterials.LE_URANIUM.get(MIMaterialParts.FUEL_ROD_DOUBLE).asItem())
            .add(MIMaterials.LE_URANIUM.get(MIMaterialParts.FUEL_ROD_QUAD).asItem())

        tag(YAITags.MI_FUEL_RODS_HE_URANIUM)
            .add(MIMaterials.HE_URANIUM.get(MIMaterialParts.FUEL_ROD).asItem())
            .add(MIMaterials.HE_URANIUM.get(MIMaterialParts.FUEL_ROD_DOUBLE).asItem())
            .add(MIMaterials.HE_URANIUM.get(MIMaterialParts.FUEL_ROD_QUAD).asItem())

        tag(YAITags.MI_FUEL_RODS_LE_MOX)
            .add(MIMaterials.LE_MOX.get(MIMaterialParts.FUEL_ROD).asItem())
            .add(MIMaterials.LE_MOX.get(MIMaterialParts.FUEL_ROD_DOUBLE).asItem())
            .add(MIMaterials.LE_MOX.get(MIMaterialParts.FUEL_ROD_QUAD).asItem())

        tag(YAITags.MI_FUEL_RODS_HE_MOX)
            .add(MIMaterials.HE_MOX.get(MIMaterialParts.FUEL_ROD).asItem())
            .add(MIMaterials.HE_MOX.get(MIMaterialParts.FUEL_ROD_DOUBLE).asItem())
            .add(MIMaterials.HE_MOX.get(MIMaterialParts.FUEL_ROD_QUAD).asItem())

        tag(YAITags.LIFESPAN_DURABILITY_TOOLTIP)
            .add(YAIItems.DEMON_CORE.get())

        tag(YAITags.GUIDE_BOOK_OR_BOOK)
            .add(Items.BOOK)
            .addTag(YAITags.MI_GUIDE_BOOKS)

        curiosTag("head")
            .add(YAIItems.INDUSTRIALISTS_GOGGLES.get())
    }

    private fun curiosTag(path: String): IntrinsicTagAppender<Item> {
        return tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", path)))
    }

}