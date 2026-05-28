package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory
import aztech.modern_industrialization.client.machines.guicomponents.EnergyBarClient
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity
import me.luligabi.yet_another_industrialization.common.misc.datamap.FlightPylonTier
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.block.Block
import java.util.function.Consumer

class FlightPylonTierCategory : ViewerCategory<FlightPylonTierCategory.Data>(
    Data::class.java,
    YAI.id(ID),
    YAI.TEXT.flightPylonTiers(),
    ItemStack(YAIMachines.getMachineFromId(FlightPylonBlockEntity.ID)),
    145, 48
) {

    private companion object {
        const val ID = "flight_pylon_tiers"
    }

    override fun buildWorkstations(consumer: WorkstationConsumer) {
        consumer.accept(YAIMachines.getMachineFromId(FlightPylonBlockEntity.ID))
    }

    override fun buildRecipes(recipeManager: RecipeManager, registryAccess: RegistryAccess, consumer: Consumer<Data>) {
        FlightPylonTier.all().toList().sortedBy { it.second.range }.forEach {
            consumer.accept(Data(it.first, it.second))
        }
    }

    override fun buildLayout(data: Data, builder: LayoutBuilder) {
        builder.inputSlot(17, 17).variant(ItemVariant.of(data.block))
    }

    override fun buildWidgets(data: Data, widgets: WidgetList) {
        widgets.drawable { gui ->
            gui.pose().pushPose()
            gui.pose().translate(5f, 5f, 0f)
            gui.pose().scale(0.5f, 0.5f, 0.5f)
            EnergyBarClient.Renderer.renderEnergy(gui, 0, 0, 1f)
            gui.pose().popPose()
        }
        val euText = TextHelper.getEuTextTick(data.eu)
        widgets.text(euText, 15f, 5f, TextAlign.LEFT, false, true, null)

        widgets.text(data.name, 26f, 36f, TextAlign.CENTER, false, true, null)
        widgets.secondaryText(
            YAI.TEXT.flightPylonRange(data.range),
            47f, 21f
        )
    }

    override fun getRecipeId(data: Data): ResourceLocation {
        return YAI.id("/$ID/" + BuiltInRegistries.BLOCK.getKey(data.block).toString().replace(':', '_'))
    }


    class Data(key: ResourceKey<Block>, tier: FlightPylonTier) {

        val block = BuiltInRegistries.BLOCK.get(key)!!

        val name = Component.translatable(tier.translationKey)
        val range = tier.range.toInt()
        val eu = tier.eu
    }
}