package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse

import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.item.ChaChaRealSmoothItem
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseSapling
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import kotlin.jvm.optionals.getOrNull

class ArboreousGreenhouseRecipeType(id: ResourceLocation): MachineRecipeType(id) {

    override fun getRecipesWithoutCache(level: Level): Collection<RecipeHolder<MachineRecipe>> {
        val recipes = mutableListOf<RecipeHolder<MachineRecipe>>().apply {
            addAll(getManagerRecipes(level))
            addAll(getCachedRecipes(level))
        }
        return recipes
    }

    override fun getRecipesWithCache(level: ServerLevel): Collection<RecipeHolder<MachineRecipe>> {
        return getRecipesWithoutCache(level)
    }

    companion object {

        private fun getCachedRecipes(level: Level): MutableList<RecipeHolder<MachineRecipe>> {
            if (!recipeCache.isEmpty()) return recipeCache

            ArboreousGreenhouseSapling.all().forEach { (item, data) ->
                val recipeList = generate(
                    "/${ArboreousGreenhouseBlockEntity.ID}/${item.location().namespace}/${item.location().path}",
                    item,
                    data,
                    level
                )

                if (recipeList.isNullOrEmpty()) {
                    println("${item.location()} null")
                    return@forEach
                } else {
                    println("${recipeList.first().id} non-null")
                }

                recipeCache.addAll(recipeList)
            }
            return recipeCache
        }

        private fun generate(
            id: String,
            item: ResourceKey<Item>,
            data: ArboreousGreenhouseSapling,
            level: Level
        ): List<RecipeHolder<MachineRecipe>>? {
            val tier = ArboreousGreenhouseTier.get(data.tier) ?: return null
            val sapling = BuiltInRegistries.ITEM.get(item.location())

            val regularRecipe = generateBase(data, tier, sapling, level)?.let {
                RecipeHolder(YAI.id(id), it.convert() as MachineRecipe)
            } ?: return null

            val nutrientRecipe = generateBase(data, tier, sapling, level, true)?.let {
                RecipeHolder(YAI.id("$id/nutrient"), it.convert() as MachineRecipe)
            } ?: return listOf(regularRecipe)

            return listOf(regularRecipe, nutrientRecipe)
        }

        private fun generateBase(data: ArboreousGreenhouseSapling, tier: ArboreousGreenhouseTier, sapling: Item, level: Level, isNutrient: Boolean = false): MIMachineRecipeBuilder? {
            if (isNutrient && tier.nutrientFluid.isEmpty) return null

            return MIMachineRecipeBuilder(
                YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE,
                15,
                60*20
            ).apply {
                addItemInput(sapling, 1, 0f)

                data.lootData.forEach {
                    val lootTable = (level as? ServerLevel)?.server?.reloadableRegistries()
                        ?.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, it.lootTable)) ?: return@forEach

                    val params = LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, BlockPos.ZERO.toVec3())
                        .withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(BlockPos.ZERO))
                        .withParameter(LootContextParams.TOOL, ChaChaRealSmoothItem.create(level))
                        .create(LootContextParamSets.BLOCK)

                    val output = lootTable.getRandomItems(params).firstOrNull()?.item
                    if (output == null || output == Items.AIR) return@forEach

                    val amount = if (!isNutrient) it.amount else it.amount * 2
                    addItemOutput(output, amount, it.probability)
                }

                //if ((this as MIRecipeJsonAccessor).recipe.itemOutputs.isEmpty()) return null

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

        private var recipeCache = mutableListOf<RecipeHolder<MachineRecipe>>()

        fun clearCache() {
            recipeCache.clear()
        }

    }

}