package me.luligabi.yet_another_industrialization.common.util

import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import me.luligabi.yet_another_industrialization.mixin.MultiblockMachineBlockEntityAccessor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.io.File
import kotlin.math.roundToInt

fun MutableComponent.applyColor(color: ChatFormatting): MutableComponent = apply {
    style = Style.EMPTY.withColor(color).withItalic(false)
}

fun MutableComponent.applyStyle(style: Style): MutableComponent = apply {
    this.style = style.withItalic(false)
}

fun Boolean.toComponent(): MutableComponent = run {
    val color = if (this) ChatFormatting.GREEN else ChatFormatting.RED
    CommonComponents.optionStatus(this).plainCopy().applyColor(color)
}

fun Float.toPercentageString(): String {
    val percent = (this * 10000).roundToInt() / 100f
    return if (percent % 1 == 0f) {
        "${percent.toInt()}%"
    } else {
        "${percent}%"
    }
}

val MultiblockMachineBlockEntity.matchedHatches: HatchFlags
    get() = (this as MultiblockMachineBlockEntityAccessor).shapeMatcher.matchedHatches.let {
        val flags = HatchFlags.Builder()
        for (hatch in it) {
            flags.with(hatch.hatchType)
        }
        flags.build()
    }

fun File.get(path: String): File? {
    return File(this, path).takeIf { it.exists() }
}

val MACHINE_REMOVER_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xD84D2C))

val ITEM_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xFF8040))
val FLUID_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0x3946DB))
