package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.generator.NumismaticGeneratorBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import java.util.function.Consumer

class NumismaticCurrenciesCategory : ViewerCategory<NumismaticCurrenciesCategory.Data>(
    Data::class.java,
    YAI.id(ID),
    YAI.TEXT.numismaticCurrencies(),
    ItemStack(YAIMachines.getMachineFromId(NumismaticGeneratorBlockEntity.ID)),
    150, 35
) {

    private companion object {
        const val ID = "numismatic_currencies"
    }

    override fun buildWorkstations(consumer: WorkstationConsumer) {
        consumer.accept(YAIMachines.getMachineFromId(NumismaticGeneratorBlockEntity.ID))
    }

    override fun buildRecipes(recipeManager: RecipeManager, registryAccess: RegistryAccess, consumer: Consumer<Data>) {
        for (item in registryAccess.registryOrThrow(Registries.ITEM)) {
            item.builtInRegistryHolder().getData(YAIDataMaps.NUMISMATIC_GENERATOR_CURRENCY)?.let {
                consumer.accept(Data(item, it.euPerItem))
            }
        }
    }

    override fun buildLayout(data: Data, builder: LayoutBuilder) {
        builder.inputSlot(15, 10).variant(ItemVariant.of(data.item))
    }

    override fun buildWidgets(data: Data, widgets: WidgetList) {
        val text = YAI.TEXT.euPerItem(data.eu)
        widgets.secondaryText(text, 40.0f, 14.0f)
    }

    override fun getRecipeId(data: Data): ResourceLocation {
        return YAI.id("/$ID/" + BuiltInRegistries.ITEM.getKey(data.item).toString().replace(':', '_'))
    }

    class Data(val item: Item, val eu: Long)
}