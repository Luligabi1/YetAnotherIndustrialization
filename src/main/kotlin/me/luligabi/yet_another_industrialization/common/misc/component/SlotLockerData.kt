package me.luligabi.yet_another_industrialization.common.misc.component

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.util.StringRepresentable

data class SlotLockerData(
    var itemVariant: ItemVariant = ItemVariant.blank(),
    var fluidVariant: FluidVariant = FluidVariant.blank(),
    var mode: Mode = Mode.LOCK_ITEM
) {

    fun isEmpty() = this == EMPTY

    enum class Mode(
        val id: String,
        val lockItem: Boolean?, // null -> ignore
        val lockFluid: Boolean?,
        val text: YAIText
    ): StringRepresentable {

        LOCK_ITEM("lock_item", true, null, YAIText.SLOT_LOCKER_MODE_LOCK_ITEM),
        LOCK_FLUID("lock_fluid", null, true, YAIText.SLOT_LOCKER_MODE_LOCK_FLUID),
        LOCK_BOTH("lock_both", true, true, YAIText.SLOT_LOCKER_MODE_LOCK_BOTH),
        UNLOCK_ITEM("unlock_item", false, null, YAIText.SLOT_LOCKER_MODE_UNLOCK_ITEM),
        UNLOCK_FLUID("unlock_fluid", null, false, YAIText.SLOT_LOCKER_MODE_UNLOCK_FLUID),
        UNLOCK_BOTH("unlock_both", false, false, YAIText.SLOT_LOCKER_MODE_UNLOCK_BOTH);

        override fun getSerializedName() = id

        fun next(): Mode {
            return Mode.entries[(this.ordinal + 1) % Mode.entries.size]
        }

    }

    companion object {

        val EMPTY = SlotLockerData()

        val CODEC = RecordCodecBuilder.create {
            it.group(
                ItemVariant.CODEC
                    .optionalFieldOf("item", ItemVariant.blank())
                    .forGetter(SlotLockerData::itemVariant),
                FluidVariant.CODEC
                    .optionalFieldOf("fluid", FluidVariant.blank())
                    .forGetter(SlotLockerData::fluidVariant),
                StringRepresentable.fromEnum(Mode::values)
                    .optionalFieldOf("mode", Mode.LOCK_ITEM)
                    .forGetter(SlotLockerData::mode)
            ).apply(it, ::SlotLockerData)
        }

        val STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC)

    }


}