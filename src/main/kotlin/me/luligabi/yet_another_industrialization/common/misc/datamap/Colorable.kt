package me.luligabi.yet_another_industrialization.common.misc.datamap

import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

data class Colorable(
    val colors: Colors,
    val solutionAmount: Long = SOLUTION_AMOUNT,
    val eu: Int = EU,
    val duration: Int = DURATION
) {

    companion object {

        const val SOLUTION_AMOUNT = 50L
        const val EU = 4
        const val DURATION = 5 * 20


        val CODEC = RecordCodecBuilder.create {
            it.group(
                Colors.CODEC.fieldOf("colors").forGetter(Colorable::colors),
                MIExtraCodecs.POSITIVE_LONG.optionalFieldOf("solution_amount", SOLUTION_AMOUNT).forGetter(Colorable::solutionAmount),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", DURATION).forGetter(Colorable::duration),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("eu", EU).forGetter(Colorable::eu)
            ).apply(it, ::Colorable)
        }

        fun all() = BuiltInRegistries.ITEM.getDataMap(YAIDataMaps.COLORABLE)

    }

    data class Colors(
        val white: Item,
        val orange: Item,
        val magenta: Item,
        val lightBlue: Item,
        val yellow: Item,
        val lime: Item,
        val pink: Item,
        val gray: Item,
        val lightGray: Item,
        val cyan: Item,
        val purple: Item,
        val blue: Item,
        val brown: Item,
        val green: Item,
        val red: Item,
        val black: Item
    ) {

        constructor(resourceLocations: Iterable<ResourceLocation>) : this(
            getItem(resourceLocations, "white"),
            getItem(resourceLocations, "orange"),
            getItem(resourceLocations, "magenta"),
            getItem(resourceLocations, "light_blue"),
            getItem(resourceLocations, "yellow"),
            getItem(resourceLocations, "lime"),
            getItem(resourceLocations, "pink"),
            getItem(resourceLocations, "gray"),
            getItem(resourceLocations, "light_gray"),
            getItem(resourceLocations, "cyan"),
            getItem(resourceLocations, "purple"),
            getItem(resourceLocations, "blue"),
            getItem(resourceLocations, "brown"),
            getItem(resourceLocations, "green"),
            getItem(resourceLocations, "red"),
            getItem(resourceLocations, "black")
        )

        fun getByColor(color: DyeColor) = when (color) {
            DyeColor.WHITE -> white
            DyeColor.ORANGE -> orange
            DyeColor.MAGENTA -> magenta
            DyeColor.LIGHT_BLUE -> lightBlue
            DyeColor.YELLOW -> yellow
            DyeColor.LIME -> lime
            DyeColor.PINK -> pink
            DyeColor.GRAY -> gray
            DyeColor.LIGHT_GRAY -> lightGray
            DyeColor.CYAN -> cyan
            DyeColor.PURPLE -> purple
            DyeColor.BLUE -> blue
            DyeColor.BROWN -> brown
            DyeColor.GREEN -> green
            DyeColor.RED -> red
            DyeColor.BLACK -> black
        }

        companion object {

            private val ITEM_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.ITEM::get, BuiltInRegistries.ITEM::getKey)

            val CODEC = RecordCodecBuilder.create {
                it.group(
                    ITEM_CODEC.optionalFieldOf("white", Items.AIR).forGetter(Colors::white),
                    ITEM_CODEC.optionalFieldOf("orange", Items.AIR).forGetter(Colors::orange),
                    ITEM_CODEC.optionalFieldOf("magenta", Items.AIR).forGetter(Colors::magenta),
                    ITEM_CODEC.optionalFieldOf("light_blue", Items.AIR).forGetter(Colors::lightBlue),
                    ITEM_CODEC.optionalFieldOf("yellow", Items.AIR).forGetter(Colors::yellow),
                    ITEM_CODEC.optionalFieldOf("lime", Items.AIR).forGetter(Colors::lime),
                    ITEM_CODEC.optionalFieldOf("pink", Items.AIR).forGetter(Colors::pink),
                    ITEM_CODEC.optionalFieldOf("gray", Items.AIR).forGetter(Colors::gray),
                    ITEM_CODEC.optionalFieldOf("light_gray", Items.AIR).forGetter(Colors::lightGray),
                    ITEM_CODEC.optionalFieldOf("cyan", Items.AIR).forGetter(Colors::cyan),
                    ITEM_CODEC.optionalFieldOf("purple", Items.AIR).forGetter(Colors::purple),
                    ITEM_CODEC.optionalFieldOf("blue", Items.AIR).forGetter(Colors::blue),
                    ITEM_CODEC.optionalFieldOf("brown", Items.AIR).forGetter(Colors::brown),
                    ITEM_CODEC.optionalFieldOf("green", Items.AIR).forGetter(Colors::green),
                    ITEM_CODEC.optionalFieldOf("red", Items.AIR).forGetter(Colors::red),
                    ITEM_CODEC.optionalFieldOf("black", Items.AIR).forGetter(Colors::black),
                ).apply(it, ::Colors)
            }

            private fun getItem(set: Iterable<ResourceLocation>, colorName: String): Item {
                val match = set.firstOrNull { it.path.contains(colorName, ignoreCase = true) }
                return if (match != null) BuiltInRegistries.ITEM.get(match) else Items.AIR
            }

        }

    }

}