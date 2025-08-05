package me.luligabi.hostile_neural_industrialization.common.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

fun MutableComponent.applyColor(color: ChatFormatting): MutableComponent = apply {
    style = Style.EMPTY.withColor(color).withItalic(false)
}

fun MutableComponent.applyStyle(style: Style): MutableComponent = apply {
    this.style = style.withItalic(false)
}