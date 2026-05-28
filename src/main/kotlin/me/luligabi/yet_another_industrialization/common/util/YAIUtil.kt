package me.luligabi.yet_another_industrialization.common.util

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import me.luligabi.yet_another_industrialization.mixin.CableTierAccessor
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

fun String.toDecimalColor(): Int {
    return this.removePrefix("#").toInt(16)
}

val MACHINE_REMOVER_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xD84D2C))

val ITEM_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0xFF8040))
val FLUID_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(0x3946DB))

val CABLE_TIER_CODEC = Codec.STRING.flatXmap(
    { id ->
        parseTier(id)?.let {
            DataResult.success(id)
        } ?: DataResult.error({ "Unknown cable tier: $id" })
    },
    { DataResult.success(it) }
)

val HEX_COLOR_CODEC = Codec.STRING.comapFlatMap(
    { hex ->
        try {
            DataResult.success(hex.toDecimalColor())
        } catch (_: Exception) {
            DataResult.error { "Invalid hex code: $hex" }
        }
    },
    { color -> "#%06X".format(color) }
)

fun parseTier(value: String): CableTier? {
    return if (value == "*") {
        CableTierAccessor.getTiers().values.maxBy { it.eu }
    } else {
        CableTierAccessor.getTiers()[value]
    }
}
