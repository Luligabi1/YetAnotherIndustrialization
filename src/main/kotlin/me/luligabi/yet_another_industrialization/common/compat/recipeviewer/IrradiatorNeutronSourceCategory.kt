package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory
import aztech.modern_industrialization.client.machines.guicomponents.EnergyBarClient
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.nuclear_rod_irradiator.NuclearRodIrradiatorBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.datamap.IrradiatorNeutronSource
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import me.luligabi.yet_another_industrialization.common.util.toPercentageString
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeManager
import net.neoforged.neoforge.common.Tags
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class IrradiatorNeutronSourceCategory : ViewerCategory<IrradiatorNeutronSourceCategory.Data>(
    Data::class.java,
    YAI.id(ID),
    YAI.TEXT.irradiatorNeutronSources(),
    ItemStack(YAIMachines.getMachineFromId(NuclearRodIrradiatorBlockEntity.ID)),
    160, 42
) {

    private companion object {
        const val ID = "irradiator_neutron_sources"
    }

    override fun buildWorkstations(consumer: WorkstationConsumer) {
        consumer.accept(YAIMachines.getMachineFromId(NuclearRodIrradiatorBlockEntity.ID))
    }

    override fun buildRecipes(recipeManager: RecipeManager, registryAccess: RegistryAccess, consumer: Consumer<Data>) {
        val sources = mutableListOf<Data>()

        for (item in registryAccess.registryOrThrow(Registries.ITEM)) {
            item.builtInRegistryHolder().getData(YAIDataMaps.IRRADIATOR_NEUTRON_SOURCE)?.let { data ->

                var restrictionData: RestrictionData? = null
                data.restrictedTo.getOrNull()?.let { restriction ->
                    restriction.map(
                        { resourceKey ->
                            val item = registryAccess.registryOrThrow(Registries.ITEM)
                                .get(resourceKey)

                            val stack = ItemStack(item)
                            restrictionData = RestrictionData(listOf(stack), stack.hoverName)
                        },
                        { tagKey ->
                            val items = registryAccess.registryOrThrow(Registries.ITEM)
                                .getTag(tagKey)
                                .map { tag -> tag.map { ItemStack(it.value()) } }
                                .orElse(null)
                            val name = Component.translatable(Tags.getTagTranslationKey(tagKey))
                            restrictionData = RestrictionData(items, name)
                        }
                    )
                }

                sources.add(Data(item, data, restrictionData))
            }
        }
        sources.sortedBy { it.source.irradiation }.forEach(consumer::accept)
    }

    override fun buildLayout(data: Data, builder: LayoutBuilder) {
        val slot = builder.inputSlot(13, 17)

        when (data.source.type) {
            IrradiatorNeutronSource.Type.CONSUMPTION,
            IrradiatorNeutronSource.Type.LIFESPAN -> {
                slot.ingredient(Ingredient.of(data.item), 1, data.source.probability)
            }
            else -> slot.variant(ItemVariant.of(data.item))
        }
    }

    override fun buildWidgets(data: Data, widgets: WidgetList) {
        widgets.drawable { gui ->
            gui.pose().pushPose()
            gui.pose().translate(5f, 5f, 0f)
            gui.pose().scale(0.5f, 0.5f, 0.5f)
            EnergyBarClient.Renderer.renderEnergy(gui, 0, 0, 1f)
            gui.pose().popPose()
        }

        val y = 3.75
        val wh = 10.8

        val euText = TextHelper.getEuTextTick(data.source.eu)
        widgets.text(euText, 15f, 5f, TextAlign.LEFT, false, true, null)

        val irradiation = TextHelper.getAmount(data.source.irradiation.toDouble())
        widgets.secondaryText(
            YAI.TEXT.irradiatorNeutronSourceIrradiation(irradiation.digit, irradiation.unit),
            35f, 19f
        )
        widgets.secondaryText(
            YAI.TEXT.irradiatorNeutronSourceType(data.source.type.component),
            35f, 27f
        )

        val tooltips = mutableListOf<Component>()
        tooltips.add(YAI.TEXT.irradiatorNeutronSourceIrradiates(data.source.irradiation.toString()))
        tooltips.add(Component.empty())
        tooltips.add(data.source.type.description(
            data.source.probability.toPercentageString(),
            "${data.source.probabilityCheckCooldown / 20}"
        ))

        data.restrictionData?.let {
            widgets.drawable { gui ->
                gui.pose().pushPose()
                val itemIndex = (System.currentTimeMillis() / 1500L % it.icons.size.toLong()).toInt()
                val displayedItem = it.icons[itemIndex].copyWithCount(1)
                gui.pose().translate(width - 15.0, y, 0.0)
                gui.pose().scale(wh.toFloat() / 16f, wh.toFloat() / 16f, 1f)
                gui.renderFakeItem(displayedItem, 0, 0)
                gui.pose().popPose()
            }

            tooltips.add(Component.empty())
            tooltips.add(YAI.TEXT.irradiatorNeutronSourceRestrictedIrradiation(it.name))
        }

        widgets.tooltip(35, 3, this.width - 35, this.height - 6, tooltips)
    }

    override fun getRecipeId(data: Data): ResourceLocation {
        return YAI.id("/$ID/" + BuiltInRegistries.ITEM.getKey(data.item).toString().replace(':', '_'))
    }

    data class Data(val item: Item, val source: IrradiatorNeutronSource, val restrictionData: RestrictionData?)

    data class RestrictionData(val icons: List<ItemStack>, val name: Component)
}