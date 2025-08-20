package me.luligabi.yet_another_industrialization.common.compat.mi

import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import me.luligabi.yet_another_industrialization.common.misc.YAICreativeTab
import net.minecraft.world.level.block.entity.BlockEntityType
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookEntrypoint
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookRegistry
import net.swedz.tesseract.neoforge.registry.holder.BlockHolder
import net.swedz.tesseract.neoforge.registry.holder.ItemHolder

@MIHookEntrypoint
class YAIHookRegistry: MIHookRegistry {

    override fun blockRegistry() = YAIBlocks.Registry.BLOCKS

    override fun blockEntityRegistry() = YAIBlocks.Registry.BLOCK_ENTITIES

    override fun itemRegistry() = YAIItems.Registry.ITEMS

    override fun recipeSerializerRegistry() = YAIMachines.RecipeTypes.RECIPE_SERIALIZERS

    override fun recipeTypeRegistry() = YAIMachines.RecipeTypes.RECIPE_TYPES

    override fun onBlockRegister(holder: BlockHolder<*>) {
        YAIBlocks.Registry.include(holder)
    }

    override fun onBlockEntityRegister(type: BlockEntityType<*>) {
    }

    override fun onItemRegister(holder: ItemHolder<*>) {
        YAIItems.Registry.include(holder)
    }

    override fun onMachineRecipeTypeRegister(type: MachineRecipeType) {
    }

    override fun sortOrderMachines() = YAICreativeTab.Order.MACHINES

}