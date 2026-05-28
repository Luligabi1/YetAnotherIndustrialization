package me.luligabi.yet_another_industrialization.datagen.server.provider.recipe

import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseTierCondition
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.yet_another_industrialization.datagen.server.provider.ArboreousGreenhouseSaplingExtractor
import me.luligabi.yet_another_industrialization.datagen.server.provider.DataMapProvider
import me.luligabi.yet_another_industrialization.datagen.server.util.ArboreousGreenhouseSapling
import me.luligabi.yet_another_industrialization.mixin.LootItemAccessor
import me.luligabi.yet_another_industrialization.mixin.MIRecipeJsonAccessor
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.gametest.framework.GameTestServer
import net.minecraft.gametest.framework.TestFunction
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.FolderRepositorySource
import net.minecraft.server.packs.repository.PackRepository
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.repository.ServerPacksSource
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.LevelStorageSource
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.validation.DirectoryValidator
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import net.neoforged.neoforge.resource.ResourcePackLoader
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import java.nio.file.Paths
import kotlin.jvm.optionals.getOrNull

object ArboreousGreenhouseRecipeProvider : YAIRecipeProvider {

    override fun buildRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        val server = startFakeServer()

        for ((id, data) in ArboreousGreenhouseSaplingExtractor.SAPLING_DATA) {
            generateSaplingRecipes(
                "${ArboreousGreenhouseBlockEntity.ID}/${id.namespace}/${id.path}",
                id,
                data,
                server,
                output
            )
        }
        server.stopServer()

        buildManualArboreousGreenhouseRecipes(output, lookup)
    }

    private fun generateSaplingRecipes(
        id: String,
        itemId: ResourceLocation,
        data: ArboreousGreenhouseSapling,
        server: MinecraftServer,
        output: RecipeOutput
    ) {
        val tier = DataMapProvider.AG_TIERS[data.tier] ?: return
        val sapling = BuiltInRegistries.ITEM.get(itemId)
        if (sapling == Items.AIR) return

        val condition = ModLoadedCondition(itemId.namespace)

        generateRecipe(data, tier, sapling, server)
            ?.offerTo(output.withConditions(condition), YAI.id("$id/regular")) ?: return

        generateRecipe(data, tier, sapling, server, true)
            ?.offerTo(output.withConditions(condition), YAI.id("$id/nutrient"))
    }

    private fun generateRecipe(
        data: ArboreousGreenhouseSapling,
        tier: ArboreousGreenhouseTier,
        sapling: Item,
        server: MinecraftServer,
        isNutrient: Boolean = false
    ): MIMachineRecipeBuilder? {
        if (isNutrient && tier.nutrientFluid.isEmpty) return null

        return MIMachineRecipeBuilder(
            YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE,
            15,
            60 * 20
        ).apply {
            addItemInput(sapling, 1, 0f)

            data.lootData.forEach {
                val lootTable = server.reloadableRegistries()
                    .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, it.lootTable)) ?: return@forEach

                val output = getFirstStaticLootItemId(lootTable)
                if (output == null || output == Items.AIR) return@forEach

                val amount = if (!isNutrient) it.amount else it.amount * 2
                addItemOutput(output, amount, it.probability)
            }
            if ((this as MIRecipeJsonAccessor).recipe.itemOutputs.isEmpty()) return null

            if (!isNutrient) {
                tier.fluid.getOrNull()?.addToRecipeInput(this)
                addItemOutput(sapling, 1, 0.5f)
            } else {
                tier.nutrientFluid.getOrNull()?.addToRecipeInput(this)
                addItemOutput(sapling, 1, 1f)
            }

            addCondition(ArboreousGreenhouseTierCondition(data.tier, data.model))
        }
    }

    private fun getFirstStaticLootItemId(table: LootTable): Item? {
        val allItems = mutableListOf<Item>()

        fun collectItems(entry: LootPoolEntryContainer) {
            when (entry) {
                is LootItem -> {
                    val item = (entry as LootItemAccessor).item.value()
                    allItems += item
                }
                is CompositeEntryBase -> {
                    entry.children.forEach(::collectItems)
                }
            }
        }

        for (pool in table.pools) {
            for (entry in pool.entries) {
                collectItems(entry)
            }
        }

        // prioritizes leaves over other drops (i.e. sticks, fruits, etc)
        return allItems.firstOrNull {
            "leaves" in BuiltInRegistries.ITEM.getKey(it).path
        } ?: allItems.firstOrNull()
    }

    private fun startFakeServer(): MinecraftServer {
        val storageAccess = LevelStorageSource.createDefault(Paths.get("dummy_saves")).createAccess("dummy_world")

        val worldPath = storageAccess.getLevelPath(LevelResource.ROOT)
        val validator = DirectoryValidator({ true })

        val packRepository = PackRepository(
            ServerPacksSource(validator),
            FolderRepositorySource(worldPath.resolve("datapacks"), PackType.SERVER_DATA, PackSource.SERVER, validator)
        )
        ResourcePackLoader.populatePackRepository(packRepository, PackType.SERVER_DATA, true)

        val dummyTest = TestFunction(
            "dummy_tests",
            "dummiest_test_ever",
            "dummy_tower",
            Rotation.CLOCKWISE_90,
            20,
            10L,
            true,
            true,
            10,
            1,
            true,
            {
                it.startSequence()
                it.assertTrue(true, "no way :o")
            }
        )

        val server = GameTestServer.create(
            Thread.currentThread(),
            storageAccess,
            packRepository,
            listOf(dummyTest),
            BlockPos.ZERO
        )
        server.initServer()

        return server
    }

    private fun buildManualArboreousGreenhouseRecipes(output: RecipeOutput, lookup: HolderLookup.Provider) {
        addArboreousGreenhouseRecipe(
            "minecraft/chorus_fruit",
            Items.CHORUS_FRUIT,
            YAIFluids.DRAGONS_BREATH.asFluid(), YAIFluids.NUTRIENT_RICH_DRAGONS_BREATH.asFluid(),
            listOf(
                Triple(Items.CHORUS_FRUIT, 8, 1f),
                Triple(Items.CHORUS_FLOWER, 1, 1f)
            ),
            YAI.id("end_stone"),
            ResourceLocation.withDefaultNamespace("chorus_flower"),
            output
        )
    }

    private fun addArboreousGreenhouseRecipe(
        id: String,
        input: ItemLike,
        fluid: Fluid?, nutrientFluid: Fluid?,
        output: List<Triple<ItemLike, Int, Float>>,
        tier: ResourceLocation,
        model: ResourceLocation,
        recipeOutput: RecipeOutput
    ) {
        if (fluid != null) {
            addMachineRecipe(
                "$id/regular",
                YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE,
                15, 60*20,
                {
                    it.addItemInput(input, 1, 0f)
                    it.addFluidInput(fluid, 1_000, 1f)

                    output.forEach { (item, amount, chance) ->
                        it.addItemOutput(item, amount, chance)
                    }
                    it.addItemOutput(input, 1, 0.5f)

                    it.addCondition(ArboreousGreenhouseTierCondition(tier, model))
                },
                recipeOutput
            )
        }

        if (nutrientFluid == null) return

        addMachineRecipe(
            "${id}/nutrient",
            YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE,
            15, 60*20,
            {
                it.addItemInput(input, 1, 0f)
                it.addFluidInput(nutrientFluid, 1_000, 1f)

                output.forEach { (item, amount, chance) ->
                    it.addItemOutput(item, amount * 2, (chance * 2).coerceAtMost(1f))
                }
                it.addItemOutput(input, 1, 1f)

                it.addCondition(ArboreousGreenhouseTierCondition(tier, model))
            },
            recipeOutput
        )
    }

}