package me.luligabi.yet_another_industrialization.common.block.machine

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.compat.rei.machines.SteamMode
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.models.MachineCasing
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions
import com.google.common.collect.Maps
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseRecipeType
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.DragonSiphonBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.dragon_siphon.EnergyGenerationCondition
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.ConfigurableMixedStorageMachineBlockEntity
import me.luligabi.yet_another_industrialization.common.item.YAIItems
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.*

object YAIMachines {

    // yes, i know.
    const val CP_ID = "cryogenic_precipitator"
    const val CP_NAME = "Cryogenic Precipitator"

    fun singleBlockCrafting(hook: SingleBlockCraftingMachinesMIHookContext) {
        hook.builder(CP_ID, CP_NAME, RecipeTypes.CRYOGENIC_PRECIPITATOR)
            .electric()
            .builtinModel(CableTier.LV.casing, CP_ID)
            .gui(SteamMode.ELECTRIC_ONLY, {
                it.slots { slots ->
                    slots.itemInput(39, 27)
                    slots.fluidInput(57, 27)
                    slots.fluidInput(57, 45, { MIFluids.CRYOFLUID.asFluid() })
                    slots.itemOutputs(103, 27, 2, 1)
                    slots.fluidOutput(103, 45, { MIFluids.ARGON.asFluid() })
                    slots.fluidOutput(121, 45, { MIFluids.HELIUM.asFluid() })
                }
                it.progressBar(79, 34, "extract")
                it.efficiencyBar(38, 66)
                it.energyBar(14, 35)
            })
            .registerMachine()
    }

    fun singleBlockSpecial(hook: SingleBlockSpecialMachinesMIHookContext) {
        hook.builder(ConfigurableMixedStorageMachineBlockEntity.ID, ConfigurableMixedStorageMachineBlockEntity.NAME, ::ConfigurableMixedStorageMachineBlockEntity)
            .builtinModel(Casings.CONFIGURABLE_MIXED_STORAGE, ConfigurableMixedStorageMachineBlockEntity.ID, { it.front(false).active(false) })
            .registrator({
                MachineBlockEntity.registerItemApi(it)
                MachineBlockEntity.registerFluidApi(it)
            })
            .registerMachine()
    }

    fun multiblockMachines(hook: MultiblockMachinesMIHookContext) {
        hook.builder(ArboreousGreenhouseBlockEntity.ID, ArboreousGreenhouseBlockEntity.NAME, ::ArboreousGreenhouseBlockEntity)
            .builtinModel(MachineCasings.HEATPROOF, ArboreousGreenhouseBlockEntity.ID)
            .gui(SteamMode.ELECTRIC_ONLY, RecipeTypes.ARBOREOUS_GREENHOUSE, {
                it.slots { slots ->
                    slots.itemInput(56, 26)
                    slots.fluidInput(56, 44)

                    slots.itemOutputs(102, 26, 2, 2)
                }
                it.progressBar(80, 33, "extract")
            }).registerRecipeCategory()
            .registerMachine()
//        hook.register(
//            ArboreousGreenhouseBlockEntity.NAME, ArboreousGreenhouseBlockEntity.ID, ArboreousGreenhouseBlockEntity.ID,
//            MachineCasings.HEATPROOF, true, false, false, true,
//            ::ArboreousGreenhouseBlockEntity
//        )
//        Rei(ArboreousGreenhouseBlockEntity.NAME, YAI.id(ArboreousGreenhouseBlockEntity.ID), RecipeTypes.ARBOREOUS_GREENHOUSE, ProgressBar.Parameters(77, 34, "extract"))
//            .items(
//                { it.addSlot(56, 26) },
//                { it.addSlots(102, 26, 2, 2) }
//            )
//            .fluids(
//                { it.addSlot(56, 44) },
//                {}
//            )
//            .workstations(YAI.id(ArboreousGreenhouseBlockEntity.ID))
//            .register()
//        MIHookTracker.addReiCategoryName(YAI.id(ArboreousGreenhouseBlockEntity.ID), ArboreousGreenhouseBlockEntity.NAME)
        hook.builder(DragonSiphonBlockEntity.ID, DragonSiphonBlockEntity.NAME, ::DragonSiphonBlockEntity)
            .builtinModel(MachineCasings.STEEL, DragonSiphonBlockEntity.ID)
            .registerMultiblockShape(DragonSiphonBlockEntity.SHAPE)
            .gui(SteamMode.NEITHER, RecipeTypes.DRAGON_SIPHON, {
                it.slots { slots ->
                    slots.itemInput(40, 35)
                    slots.fluidInput(58, 35)
                    slots.fluidOutput(104, 35)
                }
                it.progressBar(80, 33, "extract")
            }).registerRecipeCategory()
            .registerMachine()

        hook.builder(LargeStorageUnitBlockEntity.ID, LargeStorageUnitBlockEntity.NAME, ::LargeStorageUnitBlockEntity)
            .builtinModel(Casings.BATTERY_ALLOY_MACHINE_CASING, LargeStorageUnitBlockEntity.ID)
            .registerMachine()
    }

    object RecipeTypes {

        lateinit var CRYOGENIC_PRECIPITATOR: MachineRecipeType

        lateinit var ARBOREOUS_GREENHOUSE: MachineRecipeType

        lateinit var DRAGON_SIPHON: MachineRecipeType

        val RECIPE_TYPES: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, YAI.ID)
        val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, YAI.ID)

        private val RECIPE_TYPE_NAMES = Maps.newHashMap<MachineRecipeType, String>()

        fun init(modBus: IEventBus) {
            RECIPE_TYPES.register(modBus)
            RECIPE_SERIALIZERS.register(modBus)

            MachineProcessConditions.register(YAI.id("energy_generation"), EnergyGenerationCondition.CODEC, EnergyGenerationCondition.STREAM_CODEC)
        }

        internal fun create(
            hook: MachineRecipeTypesMIHookContext,
            englishName: String,
            id: String,
            creator: (ResourceLocation) -> MachineRecipeType = ::MachineRecipeType
        ): MachineRecipeType {
            val recipeType = hook.create(id, creator)
            RECIPE_TYPE_NAMES[recipeType] = englishName
            return recipeType
        }
    }

    fun recipeTypes(hook: MachineRecipeTypesMIHookContext) {
        RecipeTypes.CRYOGENIC_PRECIPITATOR = RecipeTypes.create(hook,
            CP_NAME, CP_ID
        ).withItemInputs().withFluidInputs().withItemOutputs().withFluidOutputs()

        RecipeTypes.ARBOREOUS_GREENHOUSE = RecipeTypes.create(hook,
            ArboreousGreenhouseBlockEntity.NAME, ArboreousGreenhouseBlockEntity.ID,
            ::ArboreousGreenhouseRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs()

        RecipeTypes.DRAGON_SIPHON = RecipeTypes.create(hook,
            DragonSiphonBlockEntity.NAME, DragonSiphonBlockEntity.ID
        ).withItemInputs().withFluidInputs().withFluidOutputs()
    }

    object Casings {

        lateinit var STEEL_PLATED_END_STONE_BRICKS: MachineCasing
        lateinit var BATTERY_ALLOY_MACHINE_CASING: MachineCasing
        lateinit var CONFIGURABLE_MIXED_STORAGE: MachineCasing
    }

    fun machineCasings(hook: MachineCasingsMIHookContext) {
        Casings.STEEL_PLATED_END_STONE_BRICKS = hook.registerImitateBlock(
            YAIBlocks.SPESB_ID, YAIBlocks.STEEL_PLATED_END_STONE_BRICKS
        )

        Casings.BATTERY_ALLOY_MACHINE_CASING = hook.registerCubeAll(
            "battery_alloy_machine_casing", "Battery Alloy Machine Casing",
            YAI.id("block/battery_alloy_machine_casing")
        )

        Casings.CONFIGURABLE_MIXED_STORAGE = hook.registerCubeBottomTop(
            ConfigurableMixedStorageMachineBlockEntity.ID, ConfigurableMixedStorageMachineBlockEntity.NAME,
            YAI.id("block/casing/${ConfigurableMixedStorageMachineBlockEntity.ID}/side"),
            YAI.id("block/casing/${ConfigurableMixedStorageMachineBlockEntity.ID}/side"),
            YAI.id("block/casing/${ConfigurableMixedStorageMachineBlockEntity.ID}/top")
        )

    }

    fun hatches(hook: HatchMIHookContext) {
        hook.builder(LargeStorageUnitBlockEntity.ID, LargeStorageUnitBlockEntity.NAME)
            .special(::LargeStorageUnitHatch, true)
            .builtinModel(Casings.BATTERY_ALLOY_MACHINE_CASING, "large_storage_unit_hatch")
            .registrator(LargeStorageUnitHatch::registerEnergyApi)
            .registerMachine()
    }

    fun getMachineFromId(id: String): Item {
        return YAIItems.Registry.ITEMS.registry.get()
            .get(YAI.id(id)) ?: throw IllegalStateException("Failed to get YAI! machine with ID $id")
    }

}