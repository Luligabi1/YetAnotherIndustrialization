package me.luligabi.yet_another_industrialization.datagen.server.provider

import com.google.gson.JsonParser
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.yet_another_industrialization.common.util.get
import me.luligabi.yet_another_industrialization.datagen.server.util.ArboreousGreenhouseSapling
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.io.File
import java.util.concurrent.CompletableFuture

class ArboreousGreenhouseSaplingExtractor(private val event: GatherDataEvent): DataProvider {

    companion object {
        val SAPLING_DATA = mutableMapOf<ResourceLocation, ArboreousGreenhouseSapling>()

        val BANNED_TREES = setOf("coral", "chorus_fruit")

        private val CONVERTABLE_TIERS = mapOf(
            "dirt" to "grass_block",
            "nether_stone" to "netherrack",
            "nylium" to "netherrack"
        )
    }

    override fun run(output: CachedOutput): CompletableFuture<*> {
        val baseDir = File("../src/main/resources/bonsaigen-src/datapacks").canonicalFile

        val mods = baseDir.listFiles { it.isDirectory } ?: error("bonsaigen-src missing!")

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
                    )
                }

            }
        }
        return CompletableFuture.completedFuture(null)
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

    private fun addAGSapling(
        item: ResourceLocation,
        lootData: List<ArboreousGreenhouseSapling.LootData>,
        tier: ResourceLocation = ArboreousGreenhouseTier.DEFAULT_TIER,
        model: ResourceLocation
    ) {
        SAPLING_DATA[item] = ArboreousGreenhouseSapling(lootData, tier, model)
    }

    override fun getName() = "Arboreous Greenhouse Sapling Extractor"

}