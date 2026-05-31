package me.luligabi.yet_another_industrialization.common.block.machine.colorizer

import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent
import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import me.luligabi.yet_another_industrialization.common.block.machine.util.flexible_recipe.FlexibleRecipeComponent
import me.luligabi.yet_another_industrialization.common.misc.YAIFluids
import me.luligabi.yet_another_industrialization.common.misc.datamap.Colorable
import me.luligabi.yet_another_industrialization.common.misc.datamap.YAIDataMaps
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.fluids.crafting.FluidIngredient

class ColorizerRecipeComponent(
    inventory: MultiblockInventoryComponent,
    val colorizerComponent: ColorizerComponent,
    behavior: Behavior
): FlexibleRecipeComponent(inventory, behavior) {

    override val recipeItemInputs: List<MachineRecipe.ItemInput>
        get() {
            val item = inventory.itemInputs.find { it.isColorable() }?.variant?.item ?: return emptyList()

            val amount = colorizerComponent.countEnabled() * 4
            return listOf(MachineRecipe.ItemInput(Ingredient.of(item), amount, 1f))
        }

    override val recipeItemOutputs: List<MachineRecipe.ItemOutput>
        get() {
            val item = inventory.itemInputs.find { it.isColorable() } ?: return emptyList()
            return DyeColor.entries
                .filterIndexed { i, _ -> colorizerComponent.colors[i] }
                .map {
                    MachineRecipe.ItemOutput(
                        ItemVariant.of(item.colorable!!.colors.getByColor(it)),
                        4, 1f
                    )
                }
        }
    override val recipeFluidInputs: List<MachineRecipe.FluidInput>
        get() {
            val amount = colorizerComponent.countEnabled() * 50L
            return listOf(MachineRecipe.FluidInput(FluidIngredient.of(YAIFluids.PRIMARY_COLORS_SOLUTION.get()), amount, 1f))
        }
    override val recipeFluidOutputs: List<MachineRecipe.FluidOutput>
        get() = emptyList()

    override val recipeEu = 4L



    override fun preconditions(): Boolean {
        return colorizerComponent.anyEnabled() && !recipeItemInputs.isEmpty()
    }

    private val ConfigurableItemStack.colorable: Colorable?
        get() = variant.item.builtInRegistryHolder().getData(YAIDataMaps.COLORABLE)

    private fun ConfigurableItemStack.isColorable(): Boolean {
        return colorable != null
    }

}
