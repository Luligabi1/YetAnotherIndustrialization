package me.luligabi.yet_another_industrialization.common.item

import com.google.common.collect.Sets
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAICreativeTab
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.swedz.tesseract.neoforge.registry.SortOrder
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders
import net.swedz.tesseract.neoforge.registry.common.MICommonCapabitilies
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder
import java.util.function.Function

@Suppress("MemberVisibilityCanBePrivate")
object YAIItems {

    object Registry {

        val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(YAI.ID)
        val HOLDERS: MutableSet<ItemHolder<*>> = Sets.newHashSet()

        internal fun init(bus: IEventBus) {
            ITEMS.register(bus)
        }

        fun include(holder: ItemHolder<*>) {
            HOLDERS.add(holder)
        }

    }

    fun init(bus: IEventBus) {
        Registry.init(bus)
    }

    fun values(): Set<ItemHolder<*>> = java.util.Set.copyOf(Registry.HOLDERS)


    val GUIDEBOOK = create("guidebook", "YAI! Guidebook", ::YAIGuidebookItem, YAICreativeTab.Order.GUIDEBOOK)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()

    val MACHINE_DIAGNOSER = create("machine_diagnoser", "Machine Diagnoser", ::MachineDiagnoserItem, YAICreativeTab.Order.MACHINE_DIAGNOSER)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()

    val STORAGE_SLOT_LOCKER = create("storage_slot_locker", "Storage Slot Locker", ::StorageSlotLockerItem, YAICreativeTab.Order.STORAGE_SLOT_LOCKER)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()

    val MACHINE_REMOVER = create("machine_remover", "Machine Remover", ::MachineRemoverItem, YAICreativeTab.Order.MACHINE_REMOVER)
        .withCapabilities(MICommonCapabitilies::simpleEnergyItem)
        .withoutModel()
        .register()

    val DRAGON_EGG_SIPHON_CATALYST = create("dragon_egg_siphon_catalyst", "Dragon Egg Siphon Catalyst", ::Item, YAICreativeTab.Order.MINOR_ITEMS)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()

    val CACHACA = create("cachaca", "Cachaça", ::CachacaItem, YAICreativeTab.Order.MEME)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()

    val AI_SLOP = create("ai_slop", "AI Slop", ::AISlopItem, YAICreativeTab.Order.MEME)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()


    val ULTRADENSE_METAL_BALL_BURGER = create("ultradense_metal_ball_burger", "Ultradense Metal Ball Burger", ::UltradenseMetalBallBurgerItem, YAICreativeTab.Order.MEME)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()

    val CHA_CHA_REAL_SMOOTH = create("cha_cha_real_smooth", "Cha Cha Real Smooth", ::ChaChaRealSmoothItem, YAICreativeTab.Order.HIDDEN)
        .withoutModel()
        .register()

    val ENERGY_ZAP = create("energy_zap", "Energy Zap", ::Item, YAICreativeTab.Order.HIDDEN)
        .withModelBuilder(CommonModelBuilders::generated)
        .register()


    private fun <T : Item> create(id: String, englishName: String, creator: Function<Item.Properties, T>, sortOrder: SortOrder): ItemHolder<T> {
        val holder = ItemHolder(YAI.id(id), englishName, Registry.ITEMS, creator).sorted(sortOrder)
        Registry.include(holder)
        return holder
    }

}