package me.luligabi.yet_another_industrialization.common.misc.keybind

import com.mojang.blaze3d.platform.InputConstants
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.tools.IndustrialistsGogglesItem
import me.luligabi.yet_another_industrialization.common.misc.network.ToggleIndustrialistsGogglesPacket
import net.minecraft.Util
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.util.Lazy
import org.lwjgl.glfw.GLFW
import java.util.*


object YAIKeybinds {

    object Registry {
        private val MAPPINGS = mutableSetOf<Keybind>()

        fun init(event: RegisterKeyMappingsEvent) {
            MAPPINGS.forEach({ event.register(it.holder.get()) })
        }

        fun include(mapping: Keybind) {
            MAPPINGS.add(mapping)
        }

        val mappings: MutableSet<Keybind>
            get() = Collections.unmodifiableSet(MAPPINGS)
    }

    fun init(event: RegisterKeyMappingsEvent) {
        Registry.init(event)
    }

    fun init(modBus: IEventBus) {
        modBus.addListener(RegisterKeyMappingsEvent::class.java, YAIKeybinds::init)

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post::class.java, { event ->
            for (keybind in Registry.mappings) {
                while (keybind.holder.get().consumeClick()) {
                    keybind.action()
                }
            }
        })
    }

    val TOGGLE_INDUSTRIALISTS_GOGGLES = create(
        "toggle_industrialists_goggles",
        "Toggle Industrialist's Goggles",
        { id ->
            KeyMapping(
                id,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3,
                CATEGORY
            )
        },
        {
            val newState = IndustrialistsGogglesItem.toggleMode(Minecraft.getInstance().player!!) ?: return@create
            ToggleIndustrialistsGogglesPacket(newState).sendToServer()
        }
    )


    private fun create(
        id: String,
        englishName: String,
        creator: (String) -> KeyMapping,
        action: () -> Unit
    ): Keybind {
        val descriptionId = Util.makeDescriptionId("key", YAI.id(id))
        val keybind = Keybind(descriptionId, englishName, Lazy.of({ creator(descriptionId) }), action)
        Registry.include(keybind)
        return keybind
    }

    val CATEGORY = Util.makeDescriptionId("key.categories", YAI.id(YAI.ID))

    data class Keybind(
        val descriptionId: String,
        val englishName: String,
        val holder: Lazy<KeyMapping>,
        val action: () -> Unit
    )

}