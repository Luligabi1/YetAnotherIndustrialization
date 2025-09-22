package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.component.CustomData
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.swedz.tesseract.neoforge.registry.SortOrder
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder
import java.util.function.Supplier

object YAICreativeTab {

    private val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, YAI.Companion.ID)

    val CREATIVE_TAB = CREATIVE_MODE_TABS.register(YAI.Companion.ID, Supplier {

        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.${YAI.Companion.ID}.${YAI.Companion.ID}"))
            .icon {
                val iconStack = YAIItems.MACHINE_REMOVER.get().defaultInstance
                iconStack.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY) // hide energy bar
                iconStack
            }
            .displayItems { _, output ->

                val compareBySortOrder = Comparator.comparing { obj: ItemHolder<*> -> obj.sortOrder() }
                val compareByName: Comparator<ItemHolder<*>> = Comparator.comparing { it.identifier().id() }

                YAIItems.values().stream()
                    .filter { it.sortOrder() != Order.HIDDEN }
                    .sorted(compareBySortOrder.thenComparing(compareByName))
                    .forEach(output::accept)
            }
            .build()
    })

    fun init(bus: IEventBus) {
        CREATIVE_MODE_TABS.register(bus)
    }

    object Order {
        val GUIDEBOOK = SortOrder(-1)
        val MACHINE_DIAGNOSER = SortOrder(0)
        val STORAGE_SLOT_LOCKER = SortOrder(1)
        val MACHINE_REMOVER = SortOrder(2)
        val MACHINES = SortOrder(5)
        val MINOR_ITEMS = SortOrder(10)
        val BUCKETS = SortOrder(11)
        val MEME = SortOrder(69)

        val HIDDEN = SortOrder(1000)
    }

}