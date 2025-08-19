package me.luligabi.hostile_neural_industrialization.datagen.server.provider

import com.google.gson.JsonParser
import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.misc.HNIFluids
import me.luligabi.hostile_neural_industrialization.common.misc.HNITags
import me.luligabi.hostile_neural_industrialization.common.misc.datamap.ArboreousGreenhouseSapling
import me.luligabi.hostile_neural_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.hostile_neural_industrialization.common.misc.datamap.HNIDataMaps
import me.luligabi.hostile_neural_industrialization.common.util.get
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import net.neoforged.neoforge.common.data.DataMapProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.io.File

class HNIDataMapProvider(event: GatherDataEvent): DataMapProvider(event.generator.packOutput, event.lookupProvider) {

    private companion object {

        val BANNED_TIERS = setOf("bucket")

        val CONVERTABLE_TIERS = mapOf(
            "dirt" to "grass_block",
            "nether_stone" to "netherrack",
            "nylium" to "netherrack"
        )

    }

    override fun gather(provider: HolderLookup.Provider) {
        val baseDir = File("../src/main/resources/bonsaigen-src/datapacks").canonicalFile

        val mods = baseDir.listFiles { it.isDirectory } ?: return

        for (mod in mods) {
            mod.get("data/bonsaitrees4/data_maps/item/bonsai.json")?.let {
                val jsonElement = JsonParser.parseString(it.readText())
                val values = jsonElement.asJsonObject.getAsJsonObject("values")

                for ((key, value) in values.entrySet()) {
                    val obj = value.asJsonObject
                    val model = obj.get("model")?.asString ?: continue
                    val tier = run {
                        var id = (obj.get("valid_soil_types") as? com.google.gson.JsonArray)
                            ?.firstOrNull()?.asString?.substringAfter(":", "") ?: return@run ArboreousGreenhouseTier.DEFAULT_TIER

                        CONVERTABLE_TIERS[id]?.let { converted ->
                            id = converted
                        }

                        HNI.id(id)
                    }

                    val lootData = mutableListOf<ArboreousGreenhouseSapling.LootData>()
                    mod.get("data/bonsaitrees4/loot_table/bonsai/${mod.name}/${ResourceLocation.parse(model).path}.json")?.let { lootTable ->
                        adaptLootTables(lootTable.readText()).forEach { (name, value) ->
                            println("$name: $value")
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

        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(BlockTags.DIRT, ArboreousGreenhouseTier(
                ArboreousGreenhouseTier.DEFAULT_TIER,
                BuiltInRegistries.BLOCK.getKey(Blocks.GRASS_BLOCK),
                Blocks.GRASS_BLOCK.descriptionId, sortOrder = 0
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(Tags.Blocks.SANDS, ArboreousGreenhouseTier(
                HNI.id("sand"),
                BuiltInRegistries.BLOCK.getKey(Blocks.SAND),
                Blocks.SAND.descriptionId, sortOrder = 5
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(Tags.Blocks.STONES, ArboreousGreenhouseTier(
                HNI.id("stone"),
                BuiltInRegistries.BLOCK.getKey(Blocks.STONE),
                Blocks.STONE.descriptionId, sortOrder = 10
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(HNITags.MYCELLIUMS, ArboreousGreenhouseTier(
                HNI.id("mycelium"),
                BuiltInRegistries.BLOCK.getKey(Blocks.MYCELIUM),
                Blocks.MYCELIUM.descriptionId, sortOrder = 15
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(ResourceLocation.withDefaultNamespace("water"), ArboreousGreenhouseTier(
                HNI.id("water"),
                BuiltInRegistries.ITEM.getKey(Items.WATER_BUCKET),
                Blocks.WATER.descriptionId,
                null,
                HNIFluids.NUTRIENT_RICH_WATER.identifier().location,
                20
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(HNITags.NETHERRACK_SOILS, ArboreousGreenhouseTier(
                HNI.id("netherrack"),
                BuiltInRegistries.BLOCK.getKey(Blocks.NETHERRACK),
                Blocks.NETHERRACK.descriptionId,
                ResourceLocation.withDefaultNamespace("lava"),
                HNIFluids.NUTRIENT_RICH_LAVA.identifier().location,
                30
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(ResourceLocation.withDefaultNamespace("lava"), ArboreousGreenhouseTier(
                HNI.id("lava"),
                BuiltInRegistries.ITEM.getKey(Items.LAVA_BUCKET),
                Blocks.LAVA.descriptionId,
                null,
                HNIFluids.NUTRIENT_RICH_LAVA.identifier().location,
                35
            ), false)
        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(Tags.Blocks.END_STONES, ArboreousGreenhouseTier(
                HNI.id("end_stone"),
                BuiltInRegistries.BLOCK.getKey(Blocks.END_STONE),
                Blocks.END_STONE.descriptionId,
                HNIFluids.DRAGONS_BREATH.identifier().location,
                HNIFluids.NUTRIENT_RICH_DRAGONS_BREATH.identifier().location,
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
            if (BANNED_TIERS.any { id.endsWith(it) }) continue

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
            builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING)
                .add(item, ArboreousGreenhouseSapling(lootData, tier, model), false)
            return
        }

        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING)
            .add(item, ArboreousGreenhouseSapling(lootData, tier, model), false, ModLoadedCondition(mod))
    }

    private fun addAGTier(
        block: ResourceLocation,
        icon: ResourceLocation,
        translationKey: String,
        mod: String? = null
    ) {
        if (mod == null) {
            builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
                .add(block, ArboreousGreenhouseTier(
                    HNI.id("${block.path}"),
                    icon,
                    translationKey
                ), false)
            return
        }

        builder(HNIDataMaps.ARBOREOUS_GREENHOUSE_TIER)
            .add(block, ArboreousGreenhouseTier(
                HNI.id("${block.path}"),
                icon,
                translationKey
            ), false, ModLoadedCondition(mod))
    }

}