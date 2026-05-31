package me.luligabi.yet_another_industrialization.common.block.machine.colorizer

import aztech.modern_industrialization.machines.MachineComponent
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.DyeColor

class ColorizerComponent: MachineComponent.ServerOnly {

    val colors = BooleanArray(DyeColor.entries.size)

    fun setColor(index: Int, value: Boolean) {
        colors[index] = value

    }

    fun anyEnabled(): Boolean = colors.any()

    fun countEnabled(): Int = colors.count()


    override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        val list = ListTag()

        DyeColor.entries.forEachIndexed { i, color ->
            if (colors[i]) {
                list.add(StringTag.valueOf(color.name))
            }
        }

        tag.put(KEY, list)
    }

    override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider, isUpgradingMachine: Boolean) {
        colors.fill(false)
        val list = tag.getList(KEY, Tag.TAG_STRING.toInt())

        for (i in 0 until list.size) {
            val color = DyeColor.entries.find { it.name == list.getString(i) }
            if (color != null) colors[color.ordinal] = true
        }
    }

    private companion object {
        const val KEY = "Colorizer"
    }

}