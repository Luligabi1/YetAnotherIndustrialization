package me.luligabi.yet_another_industrialization.common.compat.recipeviewer

import aztech.modern_industrialization.api.energy.EnergyApi
import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory
import aztech.modern_industrialization.client.machines.guicomponents.ProgressBarClient
import aztech.modern_industrialization.machines.guicomponents.ProgressBar
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import dev.technici4n.grandpower.api.ILongEnergyStorage
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.item_charger.ItemChargerBlockEntity
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.neoforged.neoforge.capabilities.Capabilities
import java.util.function.Consumer

class ItemChargerCategory : ViewerCategory<ItemChargerCategory.Data>(
    Data::class.java,
    YAI.id(ItemChargerBlockEntity.ID),
    YAIMachines.getMachineFromId(ItemChargerBlockEntity.ID).name,
    ItemStack(YAIMachines.getMachineFromId(ItemChargerBlockEntity.ID)),
    108, 36
) {

    override fun buildWorkstations(consumer: WorkstationConsumer) {
        consumer.accept(YAIMachines.getMachineFromId(ItemChargerBlockEntity.ID))
    }

    override fun buildRecipes(recipeManager: RecipeManager, registryAccess: RegistryAccess, consumer: Consumer<Data>) {
        for (item in registryAccess.registryOrThrow(Registries.ITEM)) {
            val input = ItemStack(item)
            input.getCapability(EnergyApi.ITEM) ?: continue

            val output = ItemStack(item)
            val capability = output.getCapability(Capabilities.EnergyStorage.ITEM) ?: continue
            if (capability !is ILongEnergyStorage) {
                val max = capability.receiveEnergy(Int.MAX_VALUE, true)
                capability.receiveEnergy(max, false)
            } else {
                val max = capability.receive(Long.MAX_VALUE, true)
                capability.receive(max, false)
            }

//            val output = ItemStack(item)
//            output.getCapability(EnergyApi.ITEM)?.let {
//
//                val max = it.receive(Long.MAX_VALUE, true)
//                it.receive(max, false)
//            } ?: continue

            consumer.accept(Data(input, output))
        }
    }

    override fun buildLayout(data: Data, builder: LayoutBuilder) {
        builder.inputSlot(23, 9).variant(ItemVariant.of(data.input))
        builder.outputSlot(68, 9).variant(ItemVariant.of(data.output))
    }

    override fun buildWidgets(data: Data, widgets: WidgetList) {
        widgets.drawable {
            ProgressBarClient.renderProgress(
                it,
                0, 0,
                ProgressBar.Params(44, 7, "yai_charge", true),
                (System.currentTimeMillis().toDouble() / 15_000 % 1.0).toFloat()
            )
        }
    }

    override fun getRecipeId(data: Data): ResourceLocation {
        return YAI.id("/${ItemChargerBlockEntity.ID}/" + BuiltInRegistries.ITEM.getKey(data.input.item).toString().replace(':', '_'))
    }


    class Data(val input: ItemStack, val output: ItemStack)
}