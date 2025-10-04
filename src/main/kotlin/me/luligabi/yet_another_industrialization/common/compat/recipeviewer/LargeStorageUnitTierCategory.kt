package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.compat.viewer.abstraction.ViewerCategory
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.datamap.LargeStorageUnitTier
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.block.Block
import java.util.function.Consumer

class LargeStorageUnitTierCategory : ViewerCategory<LargeStorageUnitTierCategory.Data>(
    Data::class.java,
    YAI.id(ID),
    YAIText.LARGE_STORAGE_UNIT_TIERS.text(),
    ItemStack(YAIMachines.getMachineFromId(LargeStorageUnitBlockEntity.ID)),
    150, 42
) {

    private companion object {
        const val ID = "large_storage_unit_tiers"
    }

    override fun buildWorkstations(consumer: WorkstationConsumer) {
        consumer.accept(YAIMachines.getMachineFromId(LargeStorageUnitBlockEntity.ID))
    }

    override fun buildRecipes(recipeManager: RecipeManager, registryAccess: RegistryAccess, consumer: Consumer<Data>) {
        LargeStorageUnitTier.all().toList().sortedBy { it.second.capacity }.forEach {
            consumer.accept(Data(it.first, it.second))
        }
    }

    override fun buildLayout(data: Data, builder: LayoutBuilder) {
        builder.inputSlot(8, 10).variant(ItemVariant.of(data.block))
        builder.inputSlot(26, 10).variant(ItemVariant.of(data.hull))
    }

    override fun buildWidgets(data: Data, widgets: WidgetList) {
        widgets.secondaryText(data.name, 20f, 29f)

        val amount = TextHelper.getAmount(data.capacity)
        widgets.secondaryText(
            YAIText.LARGE_STORAGE_UNIT_TIER_CAPACITY.text(amount.digit, amount.unit),
            47f, 14f
        )
    }

    override fun getRecipeId(data: Data): ResourceLocation {
        return YAI.id("/$ID/" + BuiltInRegistries.BLOCK.getKey(data.block).toString().replace(':', '_'))
    }


    class Data(key: ResourceKey<Block>, tier: LargeStorageUnitTier) {

        val block = BuiltInRegistries.BLOCK.get(key)!!

        private val hullId = LargeStorageUnitTier.getHull(key.location(), tier.cableTier)
        val hull = BuiltInRegistries.BLOCK.get(hullId)

        val name = Component.translatable(tier.translationKey)

        val capacity = tier.capacity

    }
}