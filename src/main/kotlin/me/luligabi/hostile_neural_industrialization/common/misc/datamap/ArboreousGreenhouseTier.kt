package me.luligabi.hostile_neural_industrialization.common.misc.datamap

import aztech.modern_industrialization.util.MIExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import java.util.Optional

data class ArboreousGreenhouseTier(
    val id: ResourceLocation,
    val icon: ResourceLocation,
    val translationKey: String,
    val fluid: Optional<FluidByIdInput>,
    val nutrientFluid: Optional<FluidByIdInput>,
    val sortOrder: Int = lastCustomIndex++,
) {

    constructor(
        id: ResourceLocation,
        icon: ResourceLocation,
        translationKey: String,
        fluid: ResourceLocation? = ResourceLocation.withDefaultNamespace("water"),
        nutrientFluid: ResourceLocation? = HNIFluids.NUTRIENT_RICH_WATER.identifier().location,
        sortOrder: Int = lastCustomIndex++
    ) : this(
        id,
        icon,
        translationKey,
        Optional.ofNullable(fluid?.let { FluidByIdInput(it, 1_000, 1f) }),
        Optional.ofNullable(nutrientFluid?.let { FluidByIdInput(it, 1_000, 1f) }),
        sortOrder,
    )

    fun toRegisteredTier(): ArboreousGreenhouseBlockEntity.Tier {
        return ArboreousGreenhouseBlockEntity.Tier(
            id,
            icon,
            translationKey,
            fluid, nutrientFluid,
            sortOrder
        )
    }


    companion object {
        var lastCustomIndex = 100

        val CODEC = RecordCodecBuilder.create {
            it.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ArboreousGreenhouseTier::id),
                ResourceLocation.CODEC.fieldOf("icon").forGetter(ArboreousGreenhouseTier::icon),
                Codec.STRING.fieldOf("translation_key").forGetter(ArboreousGreenhouseTier::translationKey),
                FluidByIdInput.CODEC.optionalFieldOf("fluid").forGetter(ArboreousGreenhouseTier::fluid),
                FluidByIdInput.CODEC.optionalFieldOf("nutrient_fluid").forGetter(ArboreousGreenhouseTier::nutrientFluid),
                Codec.INT.optionalFieldOf("sort_order", lastCustomIndex).forGetter(ArboreousGreenhouseTier::sortOrder)
            ).apply(it, ::ArboreousGreenhouseTier)
        }

        val DEFAULT_TIER = HNI.id("grass_block")

        fun all() = BuiltInRegistries.BLOCK.getDataMap(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)

        fun get(id: ResourceLocation): ArboreousGreenhouseTier? {
            return all().values.find { it.id == id }
        }

    }

    data class FluidByIdInput(val fluid: ResourceLocation, val amount: Int, val probability: Float) {


        fun addToRecipeInput(recipeBuilder: MIMachineRecipeBuilder) {
            recipeBuilder.addFluidInput(BuiltInRegistries.FLUID.get(fluid), amount, probability)
        }

        companion object {
            val CODEC = RecordCodecBuilder.create {
                it.group(
                    ResourceLocation.CODEC.fieldOf("fluid").forGetter(FluidByIdInput::fluid),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(Codec.intRange(0, Integer.MAX_VALUE), "amount", 1).forGetter(FluidByIdInput::amount),
                    MIExtraCodecs.FLOAT_01.optionalFieldOf("probability", 1f).forGetter(FluidByIdInput::probability)
                ).apply(it, ::FluidByIdInput)
            }
        }

    }

}