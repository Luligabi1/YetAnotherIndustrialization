package me.luligabi.yet_another_industrialization.common.block.machine.util.flexible_recipe

import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.machines.MachineComponent
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent
import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.fluid.FluidVariant
import aztech.modern_industrialization.util.Simulation
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.crafting.FluidIngredient
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min

abstract class FlexibleRecipeComponent(
    val inventory: MultiblockInventoryComponent,
    val behavior: Behavior
): MachineComponent.ServerOnly {

    interface Behavior {

        fun isEnabled(): Boolean

        fun consumeEu(max: Long, simulation: Simulation): Long

    }

    open val recipeItemInputs: List<MachineRecipe.ItemInput>
        get() = emptyList()

    open val recipeItemOutputs: List<MachineRecipe.ItemOutput>
        get() = emptyList()

    open val recipeFluidInputs: List<MachineRecipe.FluidInput>
        get() = emptyList()

    open val recipeFluidOutputs: List<MachineRecipe.FluidOutput>
        get() = emptyList()

    open val recipeEu = 2L

    open val recipeDuration = 4 * 20


    protected var recipe: ResourceLocation? = null
    protected var recipeEnergy = 0L
    protected var usedEnergy = 0L

    open fun tick(): Boolean {
        if (!preconditions()) return false

        val isActive: Boolean
        val isEnabled = behavior.isEnabled()

        if (usedEnergy == 0L && isEnabled) {
            if (behavior.consumeEu(1, Simulation.SIMULATE) == 1L) {
                if (tryStartRecipe()) {
                    recipeEnergy = recipeEu * recipeDuration
                } else return false

            } else return false
        }

        var eu: Long
        if (/*recipe != null &&*/ isEnabled) {
            eu = behavior.consumeEu(min(recipeEu, recipeEnergy - usedEnergy), Simulation.ACT)
            isActive = eu > 0
            usedEnergy += eu

            if (usedEnergy == recipeEnergy) {
                putItemOutputs(false, false)
                putFluidOutputs(false, false)
                clearLocks()
                usedEnergy = 0
            }
        } else {
            isActive = false
        }

        /** clearActiveRecipeIfPossible */
        if (usedEnergy == 0L) {
            recipe = null
        }

        return isActive
    }

    open fun preconditions() = true


    fun tryContinueRecipe(): Boolean {
        if (!preconditions()) {
            usedEnergy = 0
        }

        if (putItemOutputs(true, false) && putFluidOutputs(true, false)) {
            putItemOutputs(true, true)
            putFluidOutputs(true, true)
        } else {
            return false
        }
        return true
    }


    protected fun takeItemInputs(simulate: Boolean): Boolean {
        val items = inventory.itemInputs
        val stacks = if (simulate) ConfigurableItemStack.copyList(items) else items

        var ok = true
        for (input in recipeItemInputs) {
            if (!simulate && input.probability() < 1) { // if we are not simulating, there is a chance we don't need to take this output
                if (ThreadLocalRandom.current().nextFloat() >= input.probability()) {
                    continue
                }
            }
            var remainingAmount = input.amount()
            for (stack in stacks) {
                if (stack.getAmount() > 0 && input.matches(stack.resource.toStack())) { // TODO: ItemStack creation slow?
                    val taken = min(stack.getAmount().toInt(), remainingAmount)
//                    if (taken > 0 && !simulate) { FIXME
//                        behavior.getStatsOrDummy().addUsedItems(stack.getResource().getItem(), taken.toLong())
//                    }
                    stack.decrement(taken.toLong())
                    remainingAmount -= taken
                    if (remainingAmount == 0) break
                }
            }
            if (remainingAmount > 0) ok = false
        }

        return ok
    }

    protected fun takeFluidInputs(simulate: Boolean): Boolean {
        val fluids = inventory.fluidInputs
        val stacks = if (simulate) ConfigurableFluidStack.copyList(fluids) else fluids

        var ok = true
        for (input in recipeFluidInputs) {
            if (!simulate && input.probability() < 1) { // if we are not simulating, there is a chance we don't need to take this output
                if (ThreadLocalRandom.current().nextFloat() >= input.probability()) {
                    continue
                }
            }
            var remainingAmount = input.amount()
            for (stack in stacks) {
                if (fluidIngredientMatch(stack.resource, input.fluid())) {
                    val taken = min(remainingAmount, stack.getAmount())
//                    if (taken > 0 && !simulate) { // FIXME
//                        behavior.getStatsOrDummy().addUsedFluids(stack.getResource().getFluid(), taken)
//                    }
                    stack.decrement(taken)
                    remainingAmount -= taken
                    if (remainingAmount == 0L) break
                }
            }
            if (remainingAmount > 0) ok = false
        }
        return ok
    }

    private fun fluidIngredientMatch(resource: FluidVariant, ingredient: FluidIngredient): Boolean {
        if (ingredient.isSimple) {
            for (stack in ingredient.stacks) {
                if (resource == FluidVariant.of(stack.fluid)) {
                    return true
                }
            }
            return false
        } else {
            return ingredient.test(resource.toStack(1))
        }
    }

    protected fun putItemOutputs(simulate: Boolean, toggleLock: Boolean): Boolean {
        val items = inventory.itemOutputs
        val stacks = if (simulate) ConfigurableItemStack.copyList(items) else items

        val locksToToggle = mutableListOf<Int>()
        val lockItems = mutableListOf<Item>()

        var ok = true
        for (output in recipeItemOutputs) {
            if (output.probability() < 1) {
                if (simulate) continue  // don't check output space for probabilistic recipes

                val randFloat = ThreadLocalRandom.current().nextFloat()
                if (randFloat > output.probability()) continue
            }
            var remainingAmount = output.amount()
            // Try to insert in non-empty stacks or locked first, then also allow insertion
            // in empty stacks.
            for (loopRun in 0..1) {
                var stackId = 0
                for (stack in stacks) {
                    stackId++
                    val key = stack.resource
                    if (key == output.variant() || key.isBlank) {
                        // If simulating or chanced output, respect the adjusted capacity.
                        // If putting the output, don't respect the adjusted capacity in case it was
                        // reduced during the processing.
                        val remainingCapacity =
                            if (simulate || output.probability() < 1) stack.getRemainingCapacityFor(output.variant())
                                .toInt() else
                                output.variant().maxStackSize - stack.getAmount().toInt()
                        var ins = min(remainingAmount, remainingCapacity)
                        if (ins > 0) {
                            if (key.isBlank) {
                                if ((stack.isMachineLocked || stack.isPlayerLocked || loopRun == 1) && stack.isValid(
                                        output.stack
                                    )
                                ) {
                                    stack.setAmount(ins.toLong())
                                    stack.setKey(output.variant())
                                } else {
                                    ins = 0
                                }
                            } else {
                                stack.increment(ins.toLong())
                            }
                        }
                        remainingAmount -= ins
                        // ins changed inside of previous if, need to check again!
                        if (ins > 0) {
                            locksToToggle.add(stackId - 1)
                            lockItems.add(output.variant().getItem())
                            if (!simulate) {
//                                behavior.getStatsOrDummy().addProducedItems( // FIXME
//                                    behavior.getCrafterWorld(),
//                                    output.variant().item,
//                                    ins.toLong()
//                                )
                            }
                        }
                        if (remainingAmount == 0) break
                    }
                }
            }
            if (remainingAmount > 0) ok = false
        }

        if (toggleLock) {
            for (i in locksToToggle.indices) {
                items[locksToToggle[i]].enableMachineLock(lockItems[i])
            }
        }
        return ok
    }

    protected fun putFluidOutputs(simulate: Boolean, toggleLock: Boolean): Boolean {
        val fluids = inventory.fluidOutputs
        val stacks = if (simulate) ConfigurableFluidStack.copyList(fluids) else fluids

        val locksToToggle = mutableListOf<Int>()
        val lockFluids = mutableListOf<Fluid>()

        var ok = true
        for (i in 0..<min(recipeFluidOutputs.size, Integer.MAX_VALUE)) {
            val output = recipeFluidOutputs[i]
            if (output.probability() < 1) {
                if (simulate) continue  // don't check output space for probabilistic recipes

                val randFloat = ThreadLocalRandom.current().nextFloat()
                if (randFloat > output.probability()) continue
            }
            // First, try to find a slot that contains the fluid. If we couldn't find one,
            // we insert in any stack
            outer@ for (tries in 0..1) {
                for (j in stacks.indices) {
                    val stack = stacks[j]
                    val outputKey = FluidVariant.of(output.fluid())
                    if (stack.isResourceAllowedByLock(outputKey)
                        && ((tries == 1 && stack.isResourceBlank) || stack.resource == outputKey)
                    ) {
                        val inserted = min(output.amount(), stack.remainingSpace)
                        if (inserted > 0) {
                            stack.setKey(outputKey)
                            stack.increment(inserted)
                            locksToToggle.add(j)
                            lockFluids.add(output.fluid())
//                            if (!simulate) { FIXME
//                                behavior.getStatsOrDummy().addProducedFluids(output.fluid(), inserted)
//                            }
                        }
                        if (inserted < output.amount()) {
                            ok = false
                        }
                        break@outer
                    }
                }
                if (tries == 1) {
                    ok = false
                }
            }
        }

        if (toggleLock) {
            for (i in locksToToggle.indices) {
                fluids[locksToToggle[i]].enableMachineLock(lockFluids[i])
            }
        }
        return ok
    }

    private fun tryStartRecipe(): Boolean {
        if (
            takeItemInputs(true) && takeFluidInputs(true) &&
            putItemOutputs(true, false) && putFluidOutputs(true, false)
        ) {
            takeItemInputs(false)
            takeFluidInputs(false)
            putItemOutputs(true, true)
            putFluidOutputs(true, true)
            return true
        } else {
            return false
        }
    }

    protected fun clearLocks() {
        for (stack in inventory.itemOutputs) {
            if (stack.isMachineLocked) stack.disableMachineLock()
        }
        for (stack in inventory.fluidOutputs) {
            if (stack.isMachineLocked) stack.disableMachineLock()
        }
    }

    override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
        recipe?.let {
            tag.putString("Recipe", it.toString())
        }
        tag.putLong("RecipeEnergy", recipeEnergy)
        tag.putLong("UsedEnergy", usedEnergy)
    }


    override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider, isUpgradingMachine: Boolean) {
        val recipeId = tag.getString("Recipe")
        if (recipeId.isNotEmpty()) {
            recipe = ResourceLocation.tryParse(recipeId)
        }
        recipeEnergy = tag.getLong("RecipeEnergy")
        usedEnergy = tag.getLong("UsedEnergy")
    }

}