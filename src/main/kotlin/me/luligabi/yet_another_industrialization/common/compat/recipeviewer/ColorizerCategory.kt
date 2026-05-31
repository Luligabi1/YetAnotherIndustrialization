package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory
import aztech.modern_industrialization.client.machines.guicomponents.EnergyBarClient
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.colorizer.ColorizerBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.datamap.Colorable
import net.minecraft.Util
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import java.util.function.Consumer


class ColorizerCategory : ViewerCategory<ColorizerCategory.Data>(
    Data::class.java,
    YAI.id(ColorizerBlockEntity.ID),
    Component.translatable(Util.makeDescriptionId("block", YAI.id(ColorizerBlockEntity.ID))),
    ItemStack(YAIMachines.getMachineFromId(ColorizerBlockEntity.ID)),
    131, 92
) {

    override fun buildWorkstations(consumer: WorkstationConsumer) {
        consumer.accept(YAIMachines.getMachineFromId(ColorizerBlockEntity.ID))
    }

    override fun buildRecipes(recipeManager: RecipeManager, registryAccess: RegistryAccess, consumer: Consumer<Data>) {
        Colorable.all().toList().forEach {
            consumer.accept(Data(it.first, it.second))
        }
    }

    override fun buildLayout(data: Data, builder: LayoutBuilder) {
        val colorable = data.colorable

        builder.inputSlot(8, 34).item(ItemStack(data.source, 4))
        builder.inputSlot(8, 52).fluid(FluidVariant.of(YAIFluids.PRIMARY_COLORS_SOLUTION.asFluid()), 50, 1f)

        builder.outputSlot(53, 16).item(ItemStack(colorable.colors.white, 4))
        builder.outputSlot(71, 16).item(ItemStack(colorable.colors.orange, 4))
        builder.outputSlot(89, 16).item(ItemStack(colorable.colors.magenta, 4))
        builder.outputSlot(107, 16).item(ItemStack(colorable.colors.lightBlue, 4))

        builder.outputSlot(53, 34).item(ItemStack(colorable.colors.yellow, 4))
        builder.outputSlot(71, 34).item(ItemStack(colorable.colors.lime, 4))
        builder.outputSlot(89, 34).item(ItemStack(colorable.colors.pink, 4))
        builder.outputSlot(107, 34).item(ItemStack(colorable.colors.gray, 4))

        builder.outputSlot(53, 52).item(ItemStack(colorable.colors.lightGray, 4))
        builder.outputSlot(71, 52).item(ItemStack(colorable.colors.cyan, 4))
        builder.outputSlot(89, 52).item(ItemStack(colorable.colors.purple, 4))
        builder.outputSlot(107, 52).item(ItemStack(colorable.colors.blue, 4))

        builder.outputSlot(53, 70).item(ItemStack(colorable.colors.brown, 4))
        builder.outputSlot(71, 70).item(ItemStack(colorable.colors.green, 4))
        builder.outputSlot(89, 70).item(ItemStack(colorable.colors.red, 4))
        builder.outputSlot(107, 70).item(ItemStack(colorable.colors.black, 4))
    }

    override fun buildWidgets(data: Data, widgets: WidgetList) {
        widgets.drawable {
            it.pose().pushPose()
            it.pose().translate(5f, 5f, 0f)
            it.pose().scale(0.5f, 0.5f, 0.5f)
            EnergyBarClient.Renderer.renderEnergy(it, 0, 0, 1f)
            it.pose().popPose()
        }
        widgets.text(
            TextHelper.getEuTextTick(data.colorable.eu.toLong()),
            15f, 5f,
            TextAlign.LEFT, false,
            true, null
        )

        widgets.text(
            MIText.BaseDurationSeconds.text(data.colorable.duration / 20.0),
            (width - 5).toFloat(), 5f,
            TextAlign.RIGHT, false,
            true, null
        )

        widgets.arrow(27, 42)
    }

    override fun getRecipeId(data: Data): ResourceLocation {
        return YAI.id("/${ColorizerBlockEntity.ID}/" + BuiltInRegistries.ITEM.getKey(data.source).toString().replace(':', '_'))
    }

    class Data(key: ResourceKey<Item>, val colorable: Colorable) {

        val source = BuiltInRegistries.ITEM.get(key)!!
    }
}