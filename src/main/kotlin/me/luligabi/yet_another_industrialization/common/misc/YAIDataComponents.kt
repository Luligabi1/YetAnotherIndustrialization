package me.luligabi.yet_another_industrialization.common.misc

import com.mojang.serialization.Codec
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.component.SlotLockerData
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("unused")
object YAIDataComponents {

    private val COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, YAI.ID)

    val SLOT_LOCKER_DATA = create("slot_locker_data", SlotLockerData.CODEC, SlotLockerData.STREAM_CODEC)

    fun init(bus: IEventBus) {
        COMPONENTS.register(bus)
    }

    private fun <D> create(
        name: String,
        codec: Codec<D>,
        streamCodec: StreamCodec<in RegistryFriendlyByteBuf, D>
    ): DeferredHolder<DataComponentType<*>, DataComponentType<D>> {
        return COMPONENTS.registerComponentType(name) {
            it.persistent(codec).networkSynchronized(streamCodec)
        }
    }

}