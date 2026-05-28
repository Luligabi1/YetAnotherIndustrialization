package me.luligabi.yet_another_industrialization.datagen.server.provider

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import com.google.gson.JsonParser
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.misc.datamap.*
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier.FluidByIdInput
import me.luligabi.yet_another_industrialization.common.util.get
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import net.neoforged.neoforge.common.data.DataMapProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.io.File
import java.util.*

class DataMapProvider(event: GatherDataEvent): DataMapProvider(event.generator.packOutput, event.lookupProvider) {

    companion object {

        val AG_TIERS = mutableMapOf<ResourceLocation, ArboreousGreenhouseTier>()

        private val BANNED_TIERS = setOf("bucket")

        private val DEFAULT_IRRADIATOR_NEUTRON_SOURCE_TIERS = hashMapOf(
            ResourceLocation.parse(MIMaterials.BERYLLIUM.getPart(MIParts.BLOCK).itemId) to IrradiatorNeutronSource(
                1280,
                15L,
                null,
                IrradiatorNeutronSource.Type.CONSUMPTION,
                0.15f,
                10*20
            ),
            YAIItems.DEMON_CORE.identifier().location to IrradiatorNeutronSource(
                4096,
                40L,
                null,
                IrradiatorNeutronSource.Type.LIFESPAN,
                0.10f,
                10*20
            )
        )

        private val DEFAULT_LARGE_STORAGE_UNIT_TIERS = hashMapOf(
            ResourceLocation.withDefaultNamespace("redstone_block") to LargeStorageUnitTier(
                204_800_000L,
                CableTier.LV
            ),
            ResourceLocation.parse(MIMaterials.SILICON.getPart(MIParts.BLOCK).itemId) to LargeStorageUnitTier(
                819_200_000L,
                CableTier.MV
            ),
            ResourceLocation.parse(MIMaterials.SODIUM.getPart(MIParts.BLOCK).itemId) to LargeStorageUnitTier(
                7_372_800_000L,
                CableTier.HV
            ),
            YAI.id("cadmium_block") to LargeStorageUnitTier(
                52_428_800_000L,
                CableTier.EV
            ),
            ResourceLocation.parse(MIMaterials.PLUTONIUM.getPart(MIParts.BLOCK).itemId) to LargeStorageUnitTier(
                819_200_000_000L,
                CableTier.SUPERCONDUCTOR
            ),
            YAIBlocks.SINGULARITY_BLOCK.identifier().location to LargeStorageUnitTier(
                Long.MAX_VALUE,
                "*", "text.yet_another_industrialization.large_storage_unit_ultimate_tier"
            )
        )

        private val DEFAULT_FLIGHT_PYLON_TIERS = hashMapOf(
            ResourceLocation.parse(MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING).itemId) to FlightPylonTier(
                24.0,
                192L,
                "text.yet_another_industrialization.flight_pylon_tier_tiny",
                "#3F3F3F"
            ),
            ResourceLocation.parse(MIMaterials.ALUMINUM.getPart(MIParts.MACHINE_CASING).itemId) to FlightPylonTier(
                48.0,
                768L,
                "text.yet_another_industrialization.flight_pylon_tier_small",
                "#3FCAFF"
            ),
            ResourceLocation.parse(MIMaterials.STAINLESS_STEEL.getPart(MIParts.MACHINE_CASING).itemId) to FlightPylonTier(
                72.0,
                3_072L,
                "text.yet_another_industrialization.flight_pylon_tier_medium",
                "#C8C8DC"
            ),
            ResourceLocation.parse(MIMaterials.TITANIUM.getPart(MIParts.MACHINE_CASING).itemId) to FlightPylonTier(
                96.0,
                12_288L,
                "text.yet_another_industrialization.flight_pylon_tier_large",
                "#DCA0F0"
            ),
            ResourceLocation.parse(MIMaterials.IRIDIUM.getPart(MIParts.MACHINE_CASING).itemId) to FlightPylonTier(
                128.0,
                32_768L,
                "text.yet_another_industrialization.flight_pylon_tier_huge",
                "#E1E6F5"
            )
        )

    }

    override fun gather(provider: HolderLookup.Provider) {
        irradiatorNeutronSource(provider)
        flightPylonTiers(provider)
        largeStorageUnit(provider)
        arboreousGreenhouseSoils(provider)
        numismaticGeneratorCurrencies(provider)
    }

    fun irradiatorNeutronSource(provider: HolderLookup.Provider) {
        DEFAULT_IRRADIATOR_NEUTRON_SOURCE_TIERS.forEach { (id, tier) ->
            builder(YAIDataMaps.IRRADIATOR_NEUTRON_SOURCE).add(id, tier, false)
        }
    }

    fun flightPylonTiers(provider: HolderLookup.Provider) {
        DEFAULT_FLIGHT_PYLON_TIERS.forEach { (id, tier) ->
            builder(YAIDataMaps.FLIGHT_PYLON_TIER).add(id, tier, false)
        }
    }

    fun largeStorageUnit(provider: HolderLookup.Provider) {
        DEFAULT_LARGE_STORAGE_UNIT_TIERS.forEach { (id, tier) ->
            builder(YAIDataMaps.LARGE_STORAGE_UNIT_TIER).add(id, tier, false)
        }
    }

    fun arboreousGreenhouseSoils(provider: HolderLookup.Provider) {
        val baseDir = File("../src/main/resources/bonsaigen-src/datapacks").canonicalFile

        val mods = baseDir.listFiles { it.isDirectory } ?: return

        for (mod in mods) {
            mod.get("data/bonsaitrees4/bonsaitrees4/soiltype")?.findSoilTypes(mod)
            mod.get("data/${mod.name}/bonsaitrees4/soiltype")?.findSoilTypes(mod)
        }

        addAGTier(YAITags.GRASS_SOILS, ArboreousGreenhouseTier.DEFAULT_TIER, ArboreousGreenhouseTier(
            ArboreousGreenhouseTier.DEFAULT_TIER,
            BuiltInRegistries.BLOCK.getKey(Blocks.GRASS_BLOCK),
            Blocks.GRASS_BLOCK.descriptionId, sortOrder = 0
        ))
        addAGTier(Tags.Blocks.SANDS, YAI.id("sand"), ArboreousGreenhouseTier(
            YAI.id("sand"),
            BuiltInRegistries.BLOCK.getKey(Blocks.SAND),
            Blocks.SAND.descriptionId,
            Optional.of(FluidByIdInput(ResourceLocation.withDefaultNamespace("water"), 500, 1f)),
            Optional.of(FluidByIdInput(YAIFluids.NUTRIENT_RICH_WATER.identifier().location, 500, 1f)),
            5
        ))
//        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
//            .add(Tags.Blocks.STONES, ArboreousGreenhouseTier(
//                YAI.id("stone"),
//                BuiltInRegistries.BLOCK.getKey(Blocks.STONE),
//                Blocks.STONE.descriptionId, sortOrder = 10
//            ), false)
        addAGTier(YAITags.MYCELLIUMS, YAI.id("mycelium"), ArboreousGreenhouseTier(
            YAI.id("mycelium"),
            BuiltInRegistries.BLOCK.getKey(Blocks.MYCELIUM),
            Blocks.MYCELIUM.descriptionId, sortOrder = 15
        ))
//        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
//            .add(ResourceLocation.withDefaultNamespace("water"), ArboreousGreenhouseTier(
//                YAI.id("water"),
//                BuiltInRegistries.ITEM.getKey(Items.WATER_BUCKET),
//                Blocks.WATER.descriptionId,
//                null,
//                YAIFluids.NUTRIENT_RICH_WATER.identifier().location,
//                20
//            ), false)
        addAGTier(YAITags.NETHERRACK_SOILS, YAI.id("netherrack"), ArboreousGreenhouseTier(
            YAI.id("netherrack"),
            BuiltInRegistries.BLOCK.getKey(Blocks.NETHERRACK),
            Blocks.NETHERRACK.descriptionId,
            ResourceLocation.withDefaultNamespace("lava"),
            YAIFluids.NUTRIENT_RICH_LAVA.identifier().location,
            30
        ))
//        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
//            .add(ResourceLocation.withDefaultNamespace("lava"), ArboreousGreenhouseTier(
//                YAI.id("lava"),
//                BuiltInRegistries.ITEM.getKey(Items.LAVA_BUCKET),
//                Blocks.LAVA.descriptionId,
//                null,
//                YAIFluids.NUTRIENT_RICH_LAVA.identifier().location,
//                35
//            ), false)
        addAGTier(Tags.Blocks.END_STONES, YAI.id("end_stone"), ArboreousGreenhouseTier(
            YAI.id("end_stone"),
            BuiltInRegistries.BLOCK.getKey(Blocks.END_STONE),
            Blocks.END_STONE.descriptionId,
            YAIFluids.DRAGONS_BREATH.identifier().location,
            YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.identifier().location,
            40
        ))
    }

    private fun File.findSoilTypes(mod: File) {
        if (!isDirectory) return

        val soilTypes = listFiles { file -> file.extension == "json" } ?: return
        for (soilType in soilTypes) {
            val jsonElement = JsonParser.parseString(soilType.readText())
            val values = jsonElement.asJsonObject

            val id = values.get("defaultItem")?.asJsonObject?.get("id")?.asString ?: continue
            if (id.startsWith("minecraft")) continue
            if (BANNED_TIERS.any(id::endsWith)) continue

            val block = ResourceLocation.tryParse(id) ?: continue
            val translationKey = values.get("translationKey")?.asString ?: continue

            addAGTier(
                block,
                block,
                translationKey,
                mod.name
            )
        }
    }

    private fun addAGTier(
        block: ResourceLocation,
        icon: ResourceLocation,
        translationKey: String,
        mod: String? = null
    ) {
        val id = YAI.id("${block.path}")
        val tier = ArboreousGreenhouseTier(id, icon, translationKey)
        if (mod == null) {
            builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
                .add(block, tier, false)
            AG_TIERS[id] = tier
            return
        }

        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(block, tier, false, ModLoadedCondition(mod))
        AG_TIERS[id] = tier
    }

    private fun addAGTier(tag: TagKey<Block>, tierId: ResourceLocation, tier: ArboreousGreenhouseTier) {
        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(tag, tier, false)
        AG_TIERS[tierId] = tier
    }

    fun numismaticGeneratorCurrencies(provider: HolderLookup.Provider) {
        builder(YAIDataMaps.NUMISMATIC_GENERATOR_CURRENCY)
            .add(Tags.Items.GEMS_EMERALD, NumismaticGeneratorCurrency(8192L), false)
    }

}