package me.luligabi.yet_another_industrialization.datagen.client.provider.model

import com.google.gson.JsonParser
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.datagen.server.provider.DataMapProvider
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.io.File
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

class ArboreousGreenhouseModelProvider(private val event: GatherDataEvent): DataProvider {

    override fun run(output: CachedOutput): CompletableFuture<*> {
        val baseDir = File("../src/main/resources/bonsaigen-src/resourcepacks").canonicalFile

        val mods = baseDir.listFiles { it.isDirectory }
        if (mods == null || mods.isEmpty()) return CompletableFuture.completedFuture(null)

        val tasks = mutableListOf<CompletableFuture<*>>()
        for (mod in mods) {
            val root = mod.resolve("assets/bonsaitrees4/models/multiblock/${mod.name}")
            if (!root.exists()) continue

            Files.walk(root.toPath()).use { paths ->
                paths.filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }.forEach { source ->
                    if (DataMapProvider.BANNED_TREES.any(source.toString()::contains)) return@forEach

                    val relative = root.toPath().relativize(source)

                    val target = event.generator.packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                        .resolve("${YAI.ID}/models/multiblock/${ArboreousGreenhouseBlockEntity.ID}/${mod.name}")
                        .resolve(relative.toString())
                    Files.createDirectories(target.parent)

                    val fileString = run {
                        val string = String(Files.readAllBytes(source), Charsets.UTF_8)
                        string.replace("bonsaitrees4:multiblockmodel", "${YAI.ID}:multiblock")
                    }
                    val json = JsonParser.parseString(fileString)
                    tasks.add(DataProvider.saveStable(output, json, target))
                }
            }
        }
        return CompletableFuture.allOf(*tasks.toTypedArray())
    }

    override fun getName() = "Arboreous Greenhouse Model Provider"

}