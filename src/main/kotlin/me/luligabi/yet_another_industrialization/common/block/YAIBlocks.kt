package me.luligabi.yet_another_industrialization.common.block

import com.google.common.collect.Sets
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAICreativeTab
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.swedz.tesseract.neoforge.registry.SortOrder
import net.swedz.tesseract.neoforge.registry.common.CommonLootTableBuilders
import net.swedz.tesseract.neoforge.registry.common.CommonModelBuilders
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder
import net.swedz.tesseract.neoforge.registry.holder.BlockWithItemHolder

object YAIBlocks {


    object Registry {
        val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(YAI.ID)
        val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, YAI.ID)

        internal val HOLDERS: MutableSet<BlockHolder<*>> = Sets.newHashSet()

        internal fun init(bus: IEventBus) {
            BLOCKS.register(bus)
            BLOCK_ENTITIES.register(bus)
        }

        fun include(holder: BlockHolder<*>) {
            HOLDERS.add(holder)
        }

    }

    fun init(bus: IEventBus) {
        Registry.init(bus)
    }

    fun values(): Set<BlockHolder<*>> = java.util.Set.copyOf(Registry.HOLDERS)


    const val SPESB_ID = "steel_plated_end_stone_bricks"
    val STEEL_PLATED_END_STONE_BRICKS = create(
        SPESB_ID, "Steel Plated End Stone Bricks",
        ::Block, ::BlockItem,
        YAICreativeTab.Order.PARTS
    )
        .withLootTable(CommonLootTableBuilders::self)
        .withModel(CommonModelBuilders::blockCubeAll)
        .register()

    fun <B: Block, I: BlockItem> create(id: String, englishName: String, blockCreator: (BlockBehaviour.Properties) -> B, itemCreator: (Block, Item.Properties) -> I, sortOrder: SortOrder): BlockWithItemHolder<B, I> {

        val holder = BlockWithItemHolder(
            YAI.id(id), englishName,
            Registry.BLOCKS, blockCreator,
            YAIItems.Registry.ITEMS, itemCreator
        )

        holder.item().sorted(sortOrder)
        Registry.include(holder)
        YAIItems.Registry.include(holder.item())
        return holder
    }

    private fun <T : Block> itemless(
        id: String, englishName: String, blockCreator: (BlockBehaviour.Properties) -> T): BlockHolder<T> {
        val holder = BlockHolder(YAI.id(id), englishName, Registry.BLOCKS, blockCreator)
        Registry.include(holder)
        return holder
    }
    
}