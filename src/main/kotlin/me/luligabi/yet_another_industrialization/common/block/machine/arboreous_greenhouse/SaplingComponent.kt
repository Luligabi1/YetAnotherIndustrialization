package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse

import aztech.modern_industrialization.machines.IComponent
import aztech.modern_industrialization.machines.components.CrafterComponent
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

    fun update(crafter: CrafterComponent, be: ArboreousGreenhouseBlockEntity) {
        val recipe = (crafter as CrafterComponentAccessor).activeRecipe?.value
        if (recipe == null) {
            if (item != null) {
                item = null
                be.sync(false)
            }
            return
        }
        if (recipe.itemInputs.isEmpty()) return

        val newItem = recipe.itemInputs[0].inputItems[0]
        if (item != newItem) {
            item = newItem
            be.sync(false)
        }
    }

    fun reset(be: ArboreousGreenhouseBlockEntity) {
        item = null
        be.sync(false)
    }

    override fun writeClientNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        if (item == null || item == Items.AIR) return
        tag.putString(KEY, BuiltInRegistries.ITEM.getKey(item!!).toString())
    }

    override fun readClientNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        val id = tag.getString(KEY)
        if (id.isEmpty()) return

        BuiltInRegistries.ITEM.get(ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.parse(id)
        ))?.let { item = it }
    }

    private companion object {
        const val KEY = "ArboreousGreenhouseSapling"
    }

}