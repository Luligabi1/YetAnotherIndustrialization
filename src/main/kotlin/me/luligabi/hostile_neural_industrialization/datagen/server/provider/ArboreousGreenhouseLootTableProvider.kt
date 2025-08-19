package me.luligabi.hostile_neural_industrialization.datagen.server.provider

import me.luligabi.hostile_neural_industrialization.common.util.get
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootTable
import java.io.File
import java.util.function.BiConsumer

class ArboreousGreenhouseLootTableProvider(): LootTableSubProvider {

    override fun generate(output: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
        val baseDir = File("../src/main/resources/bonsaigen-src/datapacks").canonicalFile
        val mods = baseDir.listFiles { it.isDirectory } ?: return

        for (mod in mods) {
            mod.get("data/bonsaitrees4/loot_table/bonsai/${mod.name}")?.let {
                if (!it.isDirectory) return@let

                val lootTables = it.listFiles { file -> file.extension == "json" } ?: return@let
                for (table in lootTables) {

//                    val newPath = Path("../src/generated/resources/data/${HNI.ID}/loot_table/${ArboreousG}/${table.nameWithoutExtension}.json")
//                    Files.copy(table.path, aaa, StandardCopyOption.REPLACE_EXISTING)
                }

            }

        }

    }

}