package me.luligabi.yet_another_industrialization.common.item.tools

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.api.machine.component.InventoryAccess
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder
import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.blockentities.GeneratorMachineBlockEntity
import aztech.modern_industrialization.machines.blockentities.SteamCraftingMachineBlockEntity
import aztech.modern_industrialization.machines.blockentities.SteamWaterPumpBlockEntity
import aztech.modern_industrialization.machines.blockentities.multiblocks.GeneratorMultiblockBlockEntity
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity
import aztech.modern_industrialization.machines.blockentities.multiblocks.NuclearReactorMultiblockBlockEntity
import aztech.modern_industrialization.machines.blockentities.multiblocks.SteamCraftingMultiblockBlockEntity
import aztech.modern_industrialization.machines.components.CrafterComponent
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.machines.components.MultiblockInventoryComponent
import aztech.modern_industrialization.machines.components.RedstoneControlComponent
import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import aztech.modern_industrialization.machines.recipe.MachineRecipe
import aztech.modern_industrialization.util.TextHelper
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.DragonSiphonBlockEntity
import me.luligabi.yet_another_industrialization.common.item.tools.MachineDiagnoserItem.DiagnosisType.Companion.sendDiagnosis
import me.luligabi.yet_another_industrialization.common.util.applyColor
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import me.luligabi.yet_another_industrialization.common.util.matchedHatches
import me.luligabi.yet_another_industrialization.mixin.CrafterComponentAccessor
import me.luligabi.yet_another_industrialization.mixin.UseOnContextAccessor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
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

        machine.components.get(RedstoneControlComponent::class.java)?.let {
            if (!it.doAllowNormalOperation(machine)) {
                diagnosis.add(DiagnosisType.REDSTONE_BLOCKED)
                return diagnosis
            }
        }

        if (machine is MultiblockMachineBlockEntity) {
            if (!machine.isShapeValid) {
                diagnosis.add(DiagnosisType.INVALID_MULTIBLOCK_SHAPE)
                return diagnosis
            }
            diagnoseHatches(machine, diagnosis)
        } else {

            if (machine is SteamCraftingMachineBlockEntity || machine is SteamWaterPumpBlockEntity) {
                if (machine.inventory.fluidStacks.diagnoseSteam(diagnosis)) return diagnosis
            } else if (machine !is GeneratorMachineBlockEntity) {
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
            var warning = true

            if (allowedHatches.any { it.allows(HatchTypes.ENERGY_INPUT) } && !matchedHatches.allows(HatchTypes.ENERGY_INPUT)) {
                diagnosis.add(DiagnosisType.NO_ENERGY_INPUT)
                warning = false
            }

            if (allowedHatches.any { it.allows(HatchTypes.ENERGY_OUTPUT) } && !matchedHatches.allows(HatchTypes.ENERGY_OUTPUT)) {
                diagnosis.add(DiagnosisType.NO_ENERGY_OUTPUT)
                warning = true
            }

            if (machine !is GeneratorMultiblockBlockEntity && machine !is DragonSiphonBlockEntity) {
                if (machine.energyComponents.all { it.eu == 0L }) {
                    diagnosis.add(if (warning) DiagnosisType.NO_ENERGY_WARNING else DiagnosisType.NO_ENERGY)
                }
            }

        } else if (machine is SteamCraftingMultiblockBlockEntity || machine is AbstractSteamMultipliedCraftingMultiblockBlockEntity) {
            (machine.multiblockInventoryComponent as? MultiblockInventoryComponent)?.fluidInputs?.diagnoseSteam(diagnosis)
        }

        if (machine is NuclearReactorMultiblockBlockEntity) { // TODO Nuclear Reactor support
            diagnosis.add(DiagnosisType.UNSUPPORTED)
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

    enum class DiagnosisType(
        val title: () -> MutableComponent,
        val description: () -> MutableComponent,
        val severity: Severity
    ) {

        NO_STEAM(
            { YAI.TEXT.diagnoserNoSteam() },
            { YAI.TEXT.diagnoserNoSteamDescription() },
            Severity.ERROR
        ),
        NO_ENERGY(
            { YAI.TEXT.diagnoserNoEnergy() },
            { YAI.TEXT.diagnoserNoEnergyDescription() },
            Severity.ERROR
        ),
        NO_ENERGY_WARNING(
            { YAI.TEXT.diagnoserNoEnergy() },
            { YAI.TEXT.diagnoserNoEnergyWarningDescription() },
            Severity.WARNING
        ),
        NO_ENERGY_INPUT(
            { YAI.TEXT.diagnoserNoEnergyInput() },
            { YAI.TEXT.diagnoserNoEnergyInputDescription() },
            Severity.ERROR
        ),
        NO_ENERGY_OUTPUT(
            { YAI.TEXT.diagnoserNoEnergyOutput() },
            { YAI.TEXT.diagnoserNoEnergyOutputDescription() },
            Severity.ERROR
        ),
        NO_RECIPE(
            { YAI.TEXT.diagnoserNoRecipe() },
            { YAI.TEXT.diagnoserNoRecipeDescription() },
            Severity.ERROR
        ),
        BANNED_RECIPE(
            { YAI.TEXT.diagnoserBannedRecipe() },
            { YAI.TEXT.diagnoserBannedRecipeDescription() },
            Severity.ERROR
        ),
        CANT_TAKE_ITEM_INPUT(
            { YAI.TEXT.diagnoserCantTakeItemInput() },
            { YAI.TEXT.diagnoserCantTakeItemInputDescription() },
            Severity.WARNING
        ),
        CANT_TAKE_FLUID_INPUT(
            { YAI.TEXT.diagnoserCantTakeFluidInput() },
            { YAI.TEXT.diagnoserCantTakeFluidInputDescription() },
            Severity.WARNING
        ),
        CANT_PUT_ITEM_OUTPUT(
            { YAI.TEXT.diagnoserCantPutItemOutput() },
            { YAI.TEXT.diagnoserCantPutItemOutputDescription() },
            Severity.ERROR
        ),
        CANT_PUT_FLUID_OUTPUT(
            { YAI.TEXT.diagnoserCantPutFluidOutput() },
            { YAI.TEXT.diagnoserCantPutFluidOutputDescription() },
            Severity.ERROR
        ),
        CANT_PUT_ITEM_OUTPUT_WARNING(
            { YAI.TEXT.diagnoserCantPutItemOutput() },
            { YAI.TEXT.diagnoserCantPutItemOutputDescription() },
            Severity.WARNING
        ),
        CANT_PUT_FLUID_OUTPUT_WARNING(
            { YAI.TEXT.diagnoserCantPutFluidOutput() },
            { YAI.TEXT.diagnoserCantPutFluidOutputDescription() },
            Severity.WARNING
        ),
        UNMET_CONDITION(
            { YAI.TEXT.diagnoserUnmetCondition() },
            { YAI.TEXT.diagnoserUnmetConditionDescription() },
            Severity.ERROR
        ),
        INVALID_MULTIBLOCK_SHAPE(
            { YAI.TEXT.diagnoserInvalidMultiblockShape() },
            { YAI.TEXT.diagnoserInvalidMultiblockShapeDescription() },
            Severity.ERROR
        ),
        REDSTONE_BLOCKED(
            { YAI.TEXT.diagnoserRedstoneBlocked() },
            { YAI.TEXT.diagnoserRedstoneBlockedDescription() },
            Severity.ERROR
        ),
        NO_LARGE_TANK_HATCH(
            { YAI.TEXT.diagnoserNoLargeTankHatch() },
            { YAI.TEXT.diagnoserNoLargeTankHatchDescription() },
            Severity.ERROR
        ),
        UNSUPPORTED(
            { YAI.TEXT.diagnoserUnsupported() },
            { YAI.TEXT.diagnoserUnsupportedDescription() },
            Severity.INFO
        );

        companion object {

            fun Player.sendDiagnosis(diagnosis: MutableSet<DiagnosisType>, machine: MachineBlockEntity) {
                val machineName = machine.blockState.block.name

                if (diagnosis.isEmpty()) {
                    sendSystemMessage(
                        YAI.TEXT.diagnoserTitleNone(machineName)
                    )
                    return
                }

                val title = YAI.TEXT.diagnoserTitle(machineName)

                val sortedList = diagnosis.sortedBy { it.severity.ordinal }
                for ((i, type) in sortedList.withIndex()) {
                    val diagnosis = Component.literal("${type.severity.icon} ").applyColor(type.severity.darkColor)

                    val message = type.title().applyColor(type.severity.lightColor)
                    diagnosis
                        .append(message)
                        .withStyle(Style.EMPTY.withHoverEvent(HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            type.description().applyStyle(TextHelper.GRAY_TEXT)
                        )))

                    if (i != sortedList.lastIndex) diagnosis.append("\n")

                    title.append(diagnosis)
                }
                sendSystemMessage(title)
            }

        }

        enum class Severity(
            val lightColor: ChatFormatting,
            val darkColor: ChatFormatting,
            val icon: String = "⚠"
        ) {

            ERROR(ChatFormatting.RED, ChatFormatting.DARK_RED),
            WARNING(ChatFormatting.YELLOW, ChatFormatting.GOLD),
            INFO(ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, "ⓘ");
        }
    }
}