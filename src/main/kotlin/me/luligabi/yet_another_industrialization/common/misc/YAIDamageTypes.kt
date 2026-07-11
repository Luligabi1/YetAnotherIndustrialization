package me.luligabi.yet_another_industrialization.common.misc

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.PulseDetonationGeneratorBlockEntity
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.level.Level


object YAIDamageTypes {

    val PULSE_DETONATION_GENERATOR = create(PulseDetonationGeneratorBlockEntity.ID)

    private fun pdg(registry: RegistryAccess): Holder<DamageType> {
        return registry.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(PULSE_DETONATION_GENERATOR)
    }

    fun pdg(level: Level): DamageSource {
        return DamageSource(pdg(level.registryAccess()))
    }

    private fun create(id: String): ResourceKey<DamageType> {
        return ResourceKey.create(Registries.DAMAGE_TYPE, YAI.id(id))
    }
}