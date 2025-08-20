package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
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
            .icon { YAIItems.MACHINE_DIAGNOSER.get().defaultInstance }
            .displayItems { _, output ->

                val compareBySortOrder = Comparator.comparing { obj: ItemHolder<*> -> obj.sortOrder() }
                val compareByName: Comparator<ItemHolder<*>> = Comparator.comparing { it.identifier().id() }

                YAIItems.values().stream()
                    .filter { it != YAIItems.CHA_CHA_REAL_SMOOTH }
                    .sorted(compareBySortOrder.thenComparing(compareByName))
                    .forEach(output::accept)
            }
            .build()
    })

    fun init(bus: IEventBus) {
        CREATIVE_MODE_TABS.register(bus)
    }

    object Order {

        val MAJOR_ITEMS = SortOrder(0)
        val MACHINES = SortOrder(1)
        val MINOR_ITEMS = SortOrder(2)
        val PARTS = SortOrder(3)
        val BUCKETS = SortOrder(4)

    }

}