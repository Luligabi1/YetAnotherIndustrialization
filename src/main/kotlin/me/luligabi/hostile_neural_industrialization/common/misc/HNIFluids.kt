package me.luligabi.hostile_neural_industrialization.common.misc

import aztech.modern_industrialization.MICapabilities
import aztech.modern_industrialization.definition.FluidDefinition
import aztech.modern_industrialization.definition.FluidTexture
import com.google.common.collect.Sets
import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.block.HNIBlocks
import me.luligabi.hostile_neural_industrialization.common.item.HNIItems
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids.Registry.HOLDERS
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import net.swedz.tesseract.neoforge.registry.MIFluidProperties
import net.swedz.tesseract.neoforge.registry.holder.FluidHolder
import net.swedz.tesseract.neoforge.registry.holder.MIFluidHolder

@Suppress("unused")
object HNIFluids {

    object Registry {

        val FLUIDS = DeferredRegister.create(Registries.FLUID, HNI.ID)
        val FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, HNI.ID)
        val HOLDERS: MutableSet<FluidHolder<*, *, *, *>> = Sets.newHashSet()

        internal fun init(bus: IEventBus) {
            FLUIDS.register(bus)
            FLUID_TYPES.register(bus)
        }

        fun include(holder: FluidHolder<*, *, *, *>) {
            HOLDERS.add(holder)
        }

    }

    fun init(bus: IEventBus) {
        Registry.init(bus)
    }

    fun values(): Set<FluidHolder<*, *, *, *>> = java.util.Set.copyOf(HOLDERS)


    val NUTRIENT_RICH_WATER = create(
        "nutrient_rich_water", "Nutrient Rich Water",
        MIFluidProperties(0xAADCF9, FluidDefinition.NEAR_OPACITY, FluidTexture.WATER_LIKE, false)
    ).register()

    val NUTRIENT_RICH_LAVA = create(
        "nutrient_rich_lava", "Nutrient Rich Lava",
        MIFluidProperties(0xA62530, FluidDefinition.FULL_OPACITY, FluidTexture.LAVA_LIKE, false)
    ).register()

    val DRAGONS_BREATH = create(
        "dragon_breath", "Dragon's Breath",
        MIFluidProperties(0xCD75A4, FluidDefinition.NEAR_OPACITY, FluidTexture.PLASMA_LIKE, true)
    ).register()

    val NUTRIENT_RICH_DRAGONS_BREATH = create(
        "nutrient_rich_dragon_breath", "Nutrient Rich Dragon's Breath",
        MIFluidProperties(0xAC2C7B, FluidDefinition.NEAR_OPACITY, FluidTexture.PLASMA_LIKE, true)
    ).register()


    private fun create(id: String, englishName: String, properties: MIFluidProperties): MIFluidHolder {
        val holder = MIFluidHolder(
            HNI.id(id), englishName,
            Registry.FLUIDS, Registry.FLUID_TYPES,
            HNIBlocks.Registry.BLOCKS,
            HNIItems.Registry.ITEMS, HNICreativeTab.Order.BUCKETS,
            properties
        )
        Registry.include(holder)
        HNIBlocks.Registry.include(holder.block())
        HNIItems.Registry.include(holder.bucketItem())

        MICapabilities.onEvent {
            it.registerItem(
                Capabilities.FluidHandler.ITEM,
                { stack, _ -> FluidBucketWrapper(stack) },
                holder.bucketItem()
            )
        }
        return holder
    }

}