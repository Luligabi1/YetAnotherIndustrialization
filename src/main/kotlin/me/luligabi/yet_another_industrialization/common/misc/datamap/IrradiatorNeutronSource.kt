package me.luligabi.yet_another_industrialization.common.misc.datamap

import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.datafixers.util.Either
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs
import net.swedz.tesseract.neoforge.helper.RegistryHelper
import java.util.*

data class IrradiatorNeutronSource(
    val irradiation: Int,
    val eu: Long,
    val restrictedTo: Optional<Either<ResourceKey<Item>, TagKey<Item>>>,
    val type: Type,
    val probability: Float,
    val probabilityCheckCooldown: Int
) {

    constructor(
        irradiation: Int,
        eu: Long,
        restrictedTo: Either<ResourceKey<Item>, TagKey<Item>>?,
        type: Type,
        probability: Float,
        probabilityCheckCooldown: Int
    ) : this(
        irradiation,
        eu,
        if (restrictedTo != null) Optional.of(restrictedTo) else Optional.empty(),
        type,
        probability,
        probabilityCheckCooldown
    )

    companion object {

        val RESTRICTED_TO_CODEC = NeoForgeExtraCodecs.xor(
            ResourceKey.codec(Registries.ITEM).fieldOf("item"),
            TagKey.codec(Registries.ITEM).fieldOf("tag")
        ).codec()

        val CODEC = RecordCodecBuilder.create { builder ->
            builder.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("irradiation").forGetter(IrradiatorNeutronSource::irradiation),
                MIExtraCodecs.POSITIVE_LONG.fieldOf("eu").forGetter(IrradiatorNeutronSource::eu),
                RESTRICTED_TO_CODEC.optionalFieldOf("restricted_to").forGetter(IrradiatorNeutronSource::restrictedTo),
                Type.CODEC.fieldOf("type").forGetter(IrradiatorNeutronSource::type),
                MIExtraCodecs.FLOAT_01.optionalFieldOf("probability", 0f).forGetter(IrradiatorNeutronSource::probability),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("probability_check_cooldown", 5*20).forGetter(IrradiatorNeutronSource::probabilityCheckCooldown),
            ).apply(builder, ::IrradiatorNeutronSource)
        }

        fun all() = BuiltInRegistries.ITEM.getDataMap(YAIDataMaps.IRRADIATOR_NEUTRON_SOURCE)

        fun getFor(item: Item): IrradiatorNeutronSource? {
            return RegistryHelper.holder(BuiltInRegistries.ITEM, item).getData(YAIDataMaps.IRRADIATOR_NEUTRON_SOURCE)
        }

    }

    enum class Type(
        private val serializedName: String,
        val component: Component,
        val description: (String, String) -> Component,
        val usesItem: Boolean
    ): StringRepresentable {

        NONE(
            "none",
            YAI.TEXT.irradiatorNeutronSourceTypeNone(),
            { chance, time -> YAI.TEXT.irradiatorNeutronSourceTypeNoneDescription(chance, time) },
            false
        ),
        CONSUMPTION(
            "consumption",
            YAI.TEXT.irradiatorNeutronSourceTypeConsumption(),
            { chance, time -> YAI.TEXT.irradiatorNeutronSourceTypeConsumptionDescription(chance, time) },
            true
        ),
        DURABILITY(
            "durability",
            YAI.TEXT.irradiatorNeutronSourceTypeDurability(),
            { chance, time -> YAI.TEXT.irradiatorNeutronSourceTypeDurabilityDescription(chance, time) },
            true
        );

        override fun getSerializedName() = serializedName

        companion object {
            val CODEC = StringRepresentable.fromEnum(Type::values)
        }

    }

}