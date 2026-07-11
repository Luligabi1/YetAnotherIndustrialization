package me.luligabi.yet_another_industrialization.datagen.server.provider

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAIDamageTypes
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.world.damagesource.DamageEffects
import net.minecraft.world.damagesource.DamageScaling
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

class DatapackProvider(event: GatherDataEvent) : DatapackBuiltinEntriesProvider(
    event.generator.packOutput,
    event.lookupProvider,
    BUILDER,
    setOf(YAI.ID)
) {
    private companion object {
        private val BUILDER: RegistrySetBuilder = RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE) { bootstrap ->
                bootstrap.register(
                    YAIDamageTypes.PULSE_DETONATION_GENERATOR,
                    DamageType(
                        YAIDamageTypes.PULSE_DETONATION_GENERATOR.location()
                            .let { "${it.namespace}.${it.path}" },
                        DamageScaling.NEVER,
                        0.1f,
                        DamageEffects.HURT,
                        DeathMessageType.DEFAULT
                    )
                )
            }
    }
}