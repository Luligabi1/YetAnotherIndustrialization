package me.luligabi.yet_another_industrialization.common.block.machine.util.components

import aztech.modern_industrialization.machines.gui.GuiComponentServer
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

class ToggleCheckbox(
    val action: (Boolean) -> Unit,
    val value: () -> Boolean,
    val tooltip: List<Component>
): GuiComponentServer<ToggleCheckbox.ParamData, Boolean> {

    override fun getParams() = ParamData(value(), tooltip)

    override fun extractData() = value()

    override fun getType() = TYPE

    companion object {

        val PARAM_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ParamData::value,
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), ParamData::tooltip,
            ::ParamData
        )

        val TYPE = GuiComponentServer.Type(
            YAI.id("toggle_checkbox"),
            PARAM_STREAM_CODEC,
            ByteBufCodecs.BOOL
        )




    }

    data class ParamData(val value: Boolean, val tooltip: List<Component>)

}