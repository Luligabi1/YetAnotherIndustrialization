package me.luligabi.yet_another_industrialization.common.misc.effect

import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.neoforged.neoforge.common.NeoForgeMod

class CreativeFlightMobEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0xCF06C2) {

    init {
        this.addAttributeModifier(
            NeoForgeMod.CREATIVE_FLIGHT,
            YAI.id("creative_flight"),
            1.0,
            AttributeModifier.Operation.ADD_VALUE
        )
    }

}