package me.luligabi.yet_another_industrialization.datagen.server.provider

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import com.google.gson.JsonParser
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseSapling
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.yet_another_industrialization.common.misc.datamap.LargeStorageUnitTier
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import me.luligabi.yet_another_industrialization.common.util.get
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import net.neoforged.neoforge.common.data.DataMapProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.io.File

class DataMapProvider(event: GatherDataEvent): DataMapProvider(event.generator.packOutput, event.lookupProvider) {

    companion object {

        val BANNED_TREES = setOf("coral", "chorus_fruit")
        private val BANNED_TIERS = setOf("bucket")

        private val CONVERTABLE_TIERS = mapOf(
            "dirt" to "grass_block",
            "nether_stone" to "netherrack",
            "nylium" to "netherrack"
        )

        private val DEFAULT_LARGE_STORAGE_UNIT_TIERS = hashMapOf(
            ResourceLocation.withDefaultNamespace("redstone_block") to LargeStorageUnitTier(
                25_600_000L,
                CableTier.LV, CableTier.LV.shortEnglishKey(),
            ),
            ResourceLocation.parse(MIMaterials.SILICON.getPart(MIParts.BLOCK).itemId) to LargeStorageUnitTier(
                102_400_000L,
                CableTier.MV, CableTier.MV.shortEnglishKey(),
            ),
            ResourceLocation.parse(MIMaterials.SODIUM.getPart(MIParts.BLOCK).itemId) to LargeStorageUnitTier(
                921_600_000L,
                CableTier.HV, CableTier.HV.shortEnglishKey(),
            ),
            YAI.id("cadmium_block") to LargeStorageUnitTier(
                6_553_600_000L,
                CableTier.EV, CableTier.EV.shortEnglishKey(),
            ),
            ResourceLocation.parse(MIMaterials.PLUTONIUM.getPart(MIParts.BLOCK).itemId) to LargeStorageUnitTier(
                102_400_000_000_000L,
                CableTier.SUPERCONDUCTOR, CableTier.SUPERCONDUCTOR.shortEnglishKey(),
            )
        )

    }

    override fun gather(provider: HolderLookup.Provider) {
        largeStorageUnit(provider)
        arboreousGreenhouse(provider)
    }

    fun largeStorageUnit(provider: HolderLookup.Provider) {
        DEFAULT_LARGE_STORAGE_UNIT_TIERS.forEach { (id, tier) ->
            builder(YAIDataMaps.LARGE_STORAGE_UNIT_TIER).add(id, tier, false)
        }
    }

    fun arboreousGreenhouse(provider: HolderLookup.Provider) {
        val baseDir = File("../src/main/resources/bonsaigen-src/datapacks").canonicalFile

        val mods = baseDir.listFiles { it.isDirectory } ?: return

        for (mod in mods) {
            mod.get("data/bonsaitrees4/data_maps/item/bonsai.json")?.let {
                val jsonElement = JsonParser.parseString(it.readText())
                val values = jsonElement.asJsonObject.getAsJsonObject("values")

                for ((key, value) in values.entrySet()) {
                    if (BANNED_TREES.any(key::contains)) continue
                    val obj = value.asJsonObject
                    val model = obj.get("model")?.asString ?: continue
                    val tier = run {
                        var id = (obj.get("valid_soil_types") as? com.google.gson.JsonArray)
                            ?.firstOrNull()?.asString?.substringAfter(":", "") ?: return@run ArboreousGreenhouseTier.DEFAULT_TIER

                        CONVERTABLE_TIERS[id]?.let { converted ->
                            id = converted
                        }

                        YAI.id(id)
                    }

                    val lootData = mutableListOf<ArboreousGreenhouseSapling.LootData>()
                    mod.get("data/bonsaitrees4/loot_table/bonsai/${mod.name}/${ResourceLocation.parse(model).path}.json")?.let { lootTable ->
                        adaptLootTables(lootTable.readText()).forEach { (name, value) ->
                            lootData.add(ArboreousGreenhouseSapling.LootData(
                                ResourceLocation.parse(value),
                                ArboreousGreenhouseSapling.LootData.getAmount(name),
                                1f
                            ))
                        }

                    }
                    if (lootData.isEmpty()) continue

                    addAGSapling(
                        ResourceLocation.parse(key),
                        lootData,
                        tier,
                        ResourceLocation.parse(model),
                        if (mod.name != "minecraft") mod.name else null
                    )
                }

            }

            mod.get("data/bonsaitrees4/bonsaitrees4/soiltype")?.findSoilTypes(mod)
            mod.get("data/${mod.name}/bonsaitrees4/soiltype")?.findSoilTypes(mod)
        }

        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(BlockTags.DIRT, ArboreousGreenhouseTier(
                ArboreousGreenhouseTier.DEFAULT_TIER,
                BuiltInRegistries.BLOCK.getKey(Blocks.GRASS_BLOCK),
                Blocks.GRASS_BLOCK.descriptionId, sortOrder = 0
            ), false)
        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(Tags.Blocks.SANDS, ArboreousGreenhouseTier(
                YAI.id("sand"),
                BuiltInRegistries.BLOCK.getKey(Blocks.SAND),
                Blocks.SAND.descriptionId, sortOrder = 5
            ), false)
//        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
//            .add(Tags.Blocks.STONES, ArboreousGreenhouseTier(
//                YAI.id("stone"),
//                BuiltInRegistries.BLOCK.getKey(Blocks.STONE),
//                Blocks.STONE.descriptionId, sortOrder = 10
//            ), false)
        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(YAITags.MYCELLIUMS, ArboreousGreenhouseTier(
                YAI.id("mycelium"),
                BuiltInRegistries.BLOCK.getKey(Blocks.MYCELIUM),
                Blocks.MYCELIUM.descriptionId, sortOrder = 15
            ), false)
//        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
//            .add(ResourceLocation.withDefaultNamespace("water"), ArboreousGreenhouseTier(
//                YAI.id("water"),
//                BuiltInRegistries.ITEM.getKey(Items.WATER_BUCKET),
//                Blocks.WATER.descriptionId,
//                null,
//                YAIFluids.NUTRIENT_RICH_WATER.identifier().location,
//                20
//            ), false)
        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(YAITags.NETHERRACK_SOILS, ArboreousGreenhouseTier(
                YAI.id("netherrack"),
                BuiltInRegistries.BLOCK.getKey(Blocks.NETHERRACK),
                Blocks.NETHERRACK.descriptionId,
                ResourceLocation.withDefaultNamespace("lava"),
                YAIFluids.NUTRIENT_RICH_LAVA.identifier().location,
                30
            ), false)
//        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
//            .add(ResourceLocation.withDefaultNamespace("lava"), ArboreousGreenhouseTier(
//                YAI.id("lava"),
//                BuiltInRegistries.ITEM.getKey(Items.LAVA_BUCKET),
//                Blocks.LAVA.descriptionId,
//                null,
//                YAIFluids.NUTRIENT_RICH_LAVA.identifier().location,
//                35
//            ), false)
        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(Tags.Blocks.END_STONES, ArboreousGreenhouseTier(
                YAI.id("end_stone"),
                BuiltInRegistries.BLOCK.getKey(Blocks.END_STONE),
                Blocks.END_STONE.descriptionId,
                YAIFluids.DRAGONS_BREATH.identifier().location,
                YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.identifier().location,
                40
            ), false)
    }

    // TODO very ugly... hard sell to spend time making datagen stuff prettier though lol
    private fun adaptLootTables(json: String): Map<String, String> {
        val nameRegex = Regex(""""name"\s*:\s*"([^"]+)"""")
        val valueRegex = Regex(""""value"\s*:\s*"([^"]+)"""")

        val result = linkedMapOf<String, String>()

        val poolsKeyIndex = json.indexOf("\"pools\"")
        if (poolsKeyIndex == -1) {
            for (nameMatch in nameRegex.findAll(json)) {
                val name = nameMatch.groupValues[1]
                val valueMatch = valueRegex.find(json, startIndex = nameMatch.range.first)
                result[name] = valueMatch?.groupValues?.get(1) ?: ""
            }
            return result
        }

        val arraysStart = json.indexOf('[', poolsKeyIndex)
        if (arraysStart == -1) return result

        var i = arraysStart + 1
        val len = json.length
        var inString = false
        var escape = false
        var depth = 0
        var objStart = -1

        while (i < len) {
            val c = json[i]
            if (inString) {
                if (escape) {
                    escape = false
                } else {
                    if (c == '\\') escape = true
                    else if (c == '"') inString = false
                }
            } else {
                if (c == '"') {
                    inString = true
                } else if (c == '{') {
                    if (depth == 0) objStart = i
                    depth++
                } else if (c == '}') {
                    depth--
                    if (depth == 0 && objStart >= 0) {
                        val poolText = json.substring(objStart, i + 1)

                        val nameMatch = nameRegex.find(poolText)
                        val name = nameMatch?.groupValues?.get(1)

                        val firstValueMatch = valueRegex.find(poolText)
                        val value = firstValueMatch?.groupValues?.get(1) ?: ""

                        if (name != null) {
                            result[name] = value
                        }

                        objStart = -1
                    }
                } else if (c == ']') {
                    if (depth == 0) break
                }
            }
            i++
        }

        return result
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

    private fun addAGSapling(
        item: ResourceLocation,
        lootData: List<ArboreousGreenhouseSapling.LootData>,
        tier: ResourceLocation = ArboreousGreenhouseTier.DEFAULT_TIER,
        model: ResourceLocation,
        mod: String? = null
    ) {
        if (mod == null) {
            builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING)
                .add(item, ArboreousGreenhouseSapling(lootData, tier, model), false)
            return
        }

        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING)
            .add(item, ArboreousGreenhouseSapling(lootData, tier, model), false, ModLoadedCondition(mod))
    }

    private fun addAGTier(
        block: ResourceLocation,
        icon: ResourceLocation,
        translationKey: String,
        mod: String? = null
    ) {
        if (mod == null) {
            builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
                .add(block, ArboreousGreenhouseTier(
                    YAI.id("${block.path}"),
                    icon,
                    translationKey
                ), false)
            return
        }

        builder(YAIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(block, ArboreousGreenhouseTier(
                YAI.id("${block.path}"),
                icon,
                translationKey
            ), false, ModLoadedCondition(mod))
    }

}