package me.luligabi.yet_another_industrialization.common.misc.effect

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffect
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

@Suppress("unused")
object YAIEffects {

    private val MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, YAI.ID)

    val CREATIVE_FLIGHT = create("creative_flight", ::CreativeFlightMobEffect)

    fun init(bus: IEventBus) {
        MOB_EFFECTS.register(bus)
    }

    private fun <E : MobEffect> create(
        name: String,
        factory: () -> E
    ): DeferredHolder<MobEffect, E> {
        return MOB_EFFECTS.register(name, factory)
    }

}