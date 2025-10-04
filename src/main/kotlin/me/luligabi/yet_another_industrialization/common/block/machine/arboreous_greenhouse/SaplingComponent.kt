package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse

import aztech.modern_industrialization.machines.IComponent
import aztech.modern_industrialization.machines.components.CrafterComponent
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

class SaplingComponent: IComponent.ClientOnly {

    var item: Item? = null
    /**
     * Used when [item] is not in [YAIDataMaps.ARBOREOUS_GREENHOUSE_SAPLING]
     * i.e. when a recipe is not generated, but added manually
     */
    var fallbackModel: ResourceLocation? = null

    fun update(crafter: CrafterComponent, be: ArboreousGreenhouseBlockEntity) {
        val recipe = (crafter as CrafterComponentAccessor).activeRecipe?.value
        if (recipe == null) {
            var changed = false

            if (item != null) {
                item = null
                changed = true
            }
            if (fallbackModel != null) {
                fallbackModel = null
                changed = true
            }

            if (changed) be.sync(false)
            return
        }
        if (recipe.itemInputs.isEmpty()) return

        val newItem = recipe.itemInputs[0].inputItems[0]
        if (item != newItem) { // new recipe
            item = newItem
            fallbackModel = recipe.conditions
                ?.filterIsInstance<ArboreousGreenhouseTierCondition>()
                ?.firstOrNull()?.model
            be.sync(false)
        }
    }

    fun reset(be: ArboreousGreenhouseBlockEntity) {
        item = null
        fallbackModel = null
        be.sync(false)
    }

    override fun writeClientNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        if (item != null && item != Items.AIR) {
            tag.putString(ITEM_KEY, BuiltInRegistries.ITEM.getKey(item!!).toString())
        }
        if (fallbackModel != null) {
            tag.putString(RECIPE_KEY, fallbackModel.toString())
        }
    }

    override fun readClientNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        val id = tag.getString(ITEM_KEY)
        if (!id.isEmpty()) {
            BuiltInRegistries.ITEM.get(ResourceKey.create(
                Registries.ITEM,
                ResourceLocation.parse(id)
            ))?.let { item = it }
        }

        ResourceLocation.tryParse(tag.getString(RECIPE_KEY))?.let {
            fallbackModel = it
        }
    }

    private companion object {
        const val ITEM_KEY = "ArboreousGreenhouseSapling"
        const val RECIPE_KEY = "ArboreousGreenhouseRecipe"
    }

}