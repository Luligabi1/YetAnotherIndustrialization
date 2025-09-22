package me.luligabi.yet_another_industrialization.common.item

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.api.machine.component.InventoryAccess
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder
import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.blockentities.SteamCraftingMachineBlockEntity
import aztech.modern_industrialization.machines.blockentities.SteamWaterPumpBlockEntity
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity
import aztech.modern_industrialization.machines.blockentities.multiblocks.SteamCraftingMultiblockBlockEntity
import aztech.modern_industrialization.machines.components.CrafterComponent
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent
import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.item.MachineDiagnoserItem.DiagnosisType.Companion.sendDiagnosis
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.common.util.applyColor
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import me.luligabi.yet_another_industrialization.common.util.matchedHatches
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor
import me.luligabi.yet_another_industrialization.mixin.UseOnContextAccessor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.phys.HitResult
import net.swedz.tesseract.neoforge.compat.mi.machine.blockentity.multiblock.multiplied.AbstractSteamMultipliedCraftingMultiblockBlockEntity

class MachineDiagnoserItem(properties: Properties) : Item(properties) {

    override fun useOn(ctx: UseOnContext): InteractionResult {
        if (ctx.level.isClientSide) return InteractionResult.FAIL
        if ((ctx as UseOnContextAccessor).hitResult.type != HitResult.Type.BLOCK) return InteractionResult.PASS

        val machine = ctx.level.getBlockEntity(ctx.clickedPos) as? MachineBlockEntity ?: return InteractionResult.PASS

        ctx.player!!.sendDiagnosis(diagnose(machine), machine)
        YAI.CONFIG.machineDiagnoser().cooldownTicks().let {
            if (it > 0) ctx.player!!.cooldowns.addCooldown(this, it)
        }
        return InteractionResult.SUCCESS
    }

    private fun diagnose(machine: MachineBlockEntity): MutableSet<DiagnosisType> {
        val diagnosis = mutableSetOf<DiagnosisType>()

        if (machine is MultiblockMachineBlockEntity) {
            if (!machine.isShapeValid) {
                diagnosis.add(DiagnosisType.INVALID_MULTIBLOCK_SHAPE)
                return diagnosis
            }
            diagnoseHatches(machine, diagnosis)
        } else {

            if (machine is SteamCraftingMachineBlockEntity || machine is SteamWaterPumpBlockEntity) {
                if (machine.inventory.fluidStacks.diagnoseSteam(diagnosis)) return diagnosis
            } else {
                machine.components.get(EnergyComponent::class.java)?.let {
                    if (it.eu == 0L) {
                        diagnosis.add(DiagnosisType.NO_ENERGY)
                        return diagnosis
                    }
                }

            }

        }

        val crafter = machine.components.get(CrafterComponent::class.java) ?: return diagnosis
        val activeRecipe = (crafter as CrafterComponentAccessor).activeRecipe
        activeRecipe?.value?.let {
            diagnoseRecipe(it, crafter, diagnosis)
        } ?: diagnosePreRecipe(crafter, diagnosis)

        return diagnosis
    }

    // TODO Nuclear Reactor support
    private fun diagnoseHatches(machine: MultiblockMachineBlockEntity, diagnosis: MutableSet<DiagnosisType>): MutableSet<DiagnosisType> {
        val allowedHatches = machine.activeShape.hatchFlags.values.toSet()
        val matchedHatches = machine.matchedHatches

        when (machine) {
            is LargeTankMultiblockBlockEntity -> {
                if (allowedHatches.any { it.allows(HatchTypes.LARGE_TANK) } && !matchedHatches.allows(HatchTypes.LARGE_TANK)) {
                    diagnosis.add(DiagnosisType.NO_LARGE_TANK_HATCH)
                    return diagnosis
                }
            }
            is MultiblockInventoryComponentHolder -> {
                diagnoseMultiblockInventory(machine.multiblockInventoryComponent, allowedHatches, diagnosis)
            }
            else -> { // if the multiblock doesn't extend AbstractCraftingMultiblockBlockEntity, they might not implement MultiblockInventoryComponentHolder
                machine.components.get(MultiblockInventoryComponent::class.java)?.let { inventory ->
                    diagnoseMultiblockInventory(inventory, allowedHatches, diagnosis)
                }
            }
        }

        if (machine is EnergyListComponentHolder) {

            var warning = true // no energy output might just be a generator that hasn't run yet

            if (allowedHatches.any { it.allows(HatchTypes.ENERGY_INPUT) } && !matchedHatches.allows(HatchTypes.ENERGY_INPUT)) {
                diagnosis.add(DiagnosisType.NO_ENERGY_INPUT)
                warning = false
            }

            if (allowedHatches.any { it.allows(HatchTypes.ENERGY_OUTPUT) } && !matchedHatches.allows(HatchTypes.ENERGY_OUTPUT)) {
                diagnosis.add(DiagnosisType.NO_ENERGY_OUTPUT)
                warning = true
            }

            if (machine.energyComponents.all { it.eu == 0L }) {
                diagnosis.add(if (warning) DiagnosisType.NO_ENERGY_WARNING else DiagnosisType.NO_ENERGY)
            }

        } else if (machine is SteamCraftingMultiblockBlockEntity || machine is AbstractSteamMultipliedCraftingMultiblockBlockEntity) {
            (machine.multiblockInventoryComponent as? MultiblockInventoryComponent)?.fluidInputs?.diagnoseSteam(diagnosis)
        }

        return diagnosis
    }

    private fun diagnoseMultiblockInventory(access: InventoryAccess, allowedHatches: Set<HatchFlags>, diagnosis: MutableSet<DiagnosisType>) {
        if (access.itemInputs.isEmpty() && allowedHatches.any { it.allows(HatchTypes.ITEM_INPUT) }) {
            diagnosis.add(DiagnosisType.CANT_TAKE_ITEM_INPUT)
        }

        if (access.itemOutputs.isEmpty() && allowedHatches.any { it.allows(HatchTypes.ITEM_OUTPUT) }) {
            diagnosis.add(DiagnosisType.CANT_PUT_ITEM_OUTPUT_WARNING)
        }

        if (access.fluidInputs.isEmpty() && allowedHatches.any { it.allows(HatchTypes.FLUID_INPUT) }) {
            diagnosis.add(DiagnosisType.CANT_TAKE_FLUID_INPUT)
        }

        if (access.fluidOutputs.isEmpty() && allowedHatches.any { it.allows(HatchTypes.FLUID_OUTPUT) }) {
            diagnosis.add(DiagnosisType.CANT_PUT_FLUID_OUTPUT_WARNING)
        }
    }

    // CrafterComponent#getRecipes
    private fun diagnosePreRecipe(crafter: CrafterComponent, diagnosis: MutableSet<DiagnosisType>) {
        val recipes = crafter.getRecipes()

        if (!recipes.iterator().hasNext()) {
            diagnosis.add(DiagnosisType.NO_RECIPE)
            return
        }

        if (recipes.all { crafter.behavior.banRecipe(it.value) }) {
            diagnosis.add(DiagnosisType.BANNED_RECIPE)
        }
    }

    // CrafterComponent#tryStartRecipe
    private fun diagnoseRecipe(recipe: MachineRecipe, crafter: CrafterComponent, diagnosis: MutableSet<DiagnosisType>) {
        if (!(crafter as CrafterComponentAccessor).invokeTakeItemInputs(recipe, true)) {
            diagnosis.add(DiagnosisType.CANT_TAKE_ITEM_INPUT)
        }

        if (!crafter.invokeTakeFluidInputs(recipe, true)) {
            diagnosis.add(DiagnosisType.CANT_TAKE_FLUID_INPUT)
        }

        if (!crafter.invokePutItemOutputs(recipe, true, false)) {
            diagnosis.add(DiagnosisType.CANT_PUT_ITEM_OUTPUT)
        }

        if (!crafter.invokePutFluidOutputs(recipe, true, false)) {
            diagnosis.add(DiagnosisType.CANT_PUT_FLUID_OUTPUT)
        }

        if (!recipe.conditionsMatch(crafter.getConditionContext())) {
            diagnosis.add(DiagnosisType.UNMET_CONDITION)
        }
    }

    // CrafterComponent#getRecipes - got rid of the hash check as it'd break checking for banned recipes
    private fun CrafterComponent.getRecipes(): Iterable<RecipeHolder<MachineRecipe>> {
        if (efficiencyTicks > 0) return emptySet()

        val serverWorld = behavior.crafterWorld
        val recipeType = behavior.recipeType()
        val recipes = recipeType.getFluidOnlyRecipes(serverWorld).toMutableSet()
        for (stack in inventory.itemInputs) {
            if (!stack.isEmpty) {
                recipes.addAll(recipeType.getMatchingRecipes(serverWorld, stack.resource.item))
            }
        }
        return recipes
    }

    private fun List<ConfigurableFluidStack>.diagnoseSteam(diagnosis: MutableSet<DiagnosisType>): Boolean {
        if (none { it.resource.fluid == MIFluids.STEAM.asFluid() && it.amount > 0 }) {
            diagnosis.add(DiagnosisType.NO_STEAM)
            return true
        }
        return false
    }

    enum class DiagnosisType(val title: YAIText, val description: YAIText, val severity: Severity) {

        NO_STEAM(YAIText.DIAGNOSER_NO_STEAM, YAIText.DIAGNOSER_NO_STEAM_DESCRIPTION, Severity.ERROR),
        NO_ENERGY(YAIText.DIAGNOSER_NO_ENERGY, YAIText.DIAGNOSER_NO_ENERGY_DESCRIPTION, Severity.ERROR),
        NO_ENERGY_WARNING(YAIText.DIAGNOSER_NO_ENERGY, YAIText.DIAGNOSER_NO_ENERGY_WARNING_DESCRIPTION, Severity.WARNING),
        NO_ENERGY_INPUT(YAIText.DIAGNOSER_NO_ENERGY_INPUT, YAIText.DIAGNOSER_NO_ENERGY_INPUT_DESCRIPTION, Severity.ERROR),
        NO_ENERGY_OUTPUT(YAIText.DIAGNOSER_NO_ENERGY_OUTPUT, YAIText.DIAGNOSER_NO_ENERGY_OUTPUT_DESCRIPTION, Severity.ERROR),
        NO_RECIPE(YAIText.DIAGNOSER_NO_RECIPE, YAIText.DIAGNOSER_NO_RECIPE_DESCRIPTION, Severity.ERROR),
        BANNED_RECIPE(YAIText.DIAGNOSER_BANNED_RECIPE, YAIText.DIAGNOSER_BANNED_RECIPE_DESCRIPTION, Severity.ERROR),
        CANT_TAKE_ITEM_INPUT(YAIText.DIAGNOSER_CANT_TAKE_ITEM_INPUT, YAIText.DIAGNOSER_CANT_TAKE_ITEM_INPUT_DESCRIPTION, Severity.WARNING),
        CANT_TAKE_FLUID_INPUT(YAIText.DIAGNOSER_CANT_TAKE_FLUID_INPUT, YAIText.DIAGNOSER_CANT_TAKE_FLUID_INPUT_DESCRIPTION, Severity.WARNING),
        CANT_PUT_ITEM_OUTPUT(YAIText.DIAGNOSER_CANT_PUT_ITEM_OUTPUT, YAIText.DIAGNOSER_CANT_PUT_ITEM_OUTPUT_DESCRIPTION, Severity.ERROR),
        CANT_PUT_FLUID_OUTPUT(YAIText.DIAGNOSER_CANT_PUT_FLUID_OUTPUT, YAIText.DIAGNOSER_CANT_PUT_FLUID_OUTPUT_DESCRIPTION, Severity.ERROR),
        CANT_PUT_ITEM_OUTPUT_WARNING(YAIText.DIAGNOSER_CANT_PUT_ITEM_OUTPUT, YAIText.DIAGNOSER_CANT_PUT_ITEM_OUTPUT_DESCRIPTION, Severity.WARNING),
        CANT_PUT_FLUID_OUTPUT_WARNING(YAIText.DIAGNOSER_CANT_PUT_FLUID_OUTPUT, YAIText.DIAGNOSER_CANT_PUT_FLUID_OUTPUT_DESCRIPTION, Severity.WARNING),
        UNMET_CONDITION(YAIText.DIAGNOSER_UNMET_CONDITION, YAIText.DIAGNOSER_UNMET_CONDITION_DESCRIPTION, Severity.ERROR),
        INVALID_MULTIBLOCK_SHAPE(YAIText.DIAGNOSER_INVALID_MULTIBLOCK_SHAPE, YAIText.DIAGNOSER_INVALID_MULTIBLOCK_SHAPE_DESCRIPTION, Severity.ERROR),
        NO_LARGE_TANK_HATCH(YAIText.DIAGNOSER_NO_LARGE_TANK_HATCH, YAIText.DIAGNOSER_NO_LARGE_TANK_HATCH_DESCRIPTION, Severity.ERROR);

        companion object {

            fun Player.sendDiagnosis(diagnosis: MutableSet<DiagnosisType>, machine: MachineBlockEntity) {
                val machineName = machine.blockState.block.name.applyStyle(TextHelper.NUMBER_TEXT)

                if (diagnosis.isEmpty()) {
                    sendSystemMessage(
                        YAIText.DIAGNOSER_TITLE_NONE.text(machineName).applyStyle(TextHelper.NEUTRONS)
                    )
                    return
                }

                val title = YAIText.DIAGNOSER_TITLE.text(machineName).applyStyle(TextHelper.GRAY_TEXT)

                val sortedList = diagnosis.sortedBy { it.severity.ordinal }
                for ((i, type) in sortedList.withIndex()) {
                    val diagnosis = Component.literal("⚠ ").applyColor(type.severity.darkColor)

                    val message = type.title.text()
                        .applyColor(type.severity.lightColor)
                    diagnosis
                        .append(message)
                        .withStyle(Style.EMPTY.withHoverEvent(HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            type.description.text().applyStyle(TextHelper.GRAY_TEXT)
                        )))

                    if (i != sortedList.lastIndex) diagnosis.append("\n")

                    title.append(diagnosis)
                }
                sendSystemMessage(title)
            }

        }

        enum class Severity(val lightColor: ChatFormatting, val darkColor: ChatFormatting) {

            ERROR(ChatFormatting.RED, ChatFormatting.DARK_RED),
            WARNING(ChatFormatting.YELLOW, ChatFormatting.GOLD)
        }
    }
}