package me.luligabi.yet_another_industrialization.common.block.machine

import aztech.modern_industrialization.MIFluids
import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.compat.rei.machines.SteamMode
import aztech.modern_industrialization.inventory.ConfigurableFluidStack
import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.inventory.SlotPositions
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.blockentities.GeneratorMachineBlockEntity
import aztech.modern_industrialization.machines.models.MachineCasing
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import aztech.modern_industrialization.machines.recipe.condition.MachineProcessConditions
import com.google.common.collect.Maps
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.YAIBlocks
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseTierCondition
import me.luligabi.yet_another_industrialization.common.block.machine.generator.NumismaticGeneratorBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.DragonSiphonBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.EnergyGenerationCondition
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.PulseDetonationGeneratorBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlock
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber.DetonationChamberCasingBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit.LargeStorageUnitHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.ConfigurableMixedStorageMachineBlockEntity
import me.luligabi.yet_another_industrialization.common.block.machine.misc.MixedHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch.FluidTrashCanHatch
import me.luligabi.yet_another_industrialization.common.block.machine.misc.trash_can_hatch.ItemTrashCanHatch
import me.luligabi.yet_another_industrialization.common.block.machine.nuclear_rod_irradiator.NuclearRodIrradiatorBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
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

        registerMixedHatches(hook)

        hook.builder(NumismaticGeneratorBlockEntity.ID, NumismaticGeneratorBlockEntity.NAME, ::NumismaticGeneratorBlockEntity)
            .builtinModel(MachineCasings.STEEL, NumismaticGeneratorBlockEntity.ID)
            .registrator(MachineBlockEntity::registerItemApi)
            .registrator(GeneratorMachineBlockEntity::registerEnergyApi)
            .registerMachine()

        hook.builder(DetonationChamberCasingBlockEntity.ID, DetonationChamberCasingBlockEntity.NAME, ::DetonationChamberCasingBlockEntity)
            .builtinModel(Casings.DETONATION_CHAMBER_CASING, DetonationChamberCasingBlockEntity.ID, { it.front(false).side(false).top(false).active(false) })
            .creator(::DetonationChamberCasingBlock)
            .registerMachine()
    }

    private fun registerMixedHatches(hook: SingleBlockSpecialMachinesMIHookContext) {
        registerMixedHatch(
            hook,
            "Bronze", "bronze",
            MachineCasings.BRONZE,
            1, 4_000L,
            SlotPositions.Builder().addSlot(62, 40).build(),
            SlotPositions.Builder().addSlot(98, 40).build()
        )
        registerMixedHatch(
            hook,
            "Steel", "steel",
            MachineCasings.STEEL,
            2, 16_000L,
            SlotPositions.Builder().addSlots(62, 30, 1, 2).build(),
            SlotPositions.Builder().addSlot(98, 39).build()
        )
        registerMixedHatch(
            hook,
            "Advanced", "advanced",
            CableTier.MV.casing,
            4, 64_000L,
            SlotPositions.Builder().addSlots(53, 30, 2, 2).build(),
            SlotPositions.Builder().addSlot(107, 39).build()
        )
        registerMixedHatch(
            hook,
            "Turbo", "turbo",
            CableTier.HV.casing,
            9, 256_000L,
            SlotPositions.Builder().addSlots(44, 21, 3, 3).build(),
            SlotPositions.Builder().addSlot(116, 39).build(),
            176
        )
        registerMixedHatch(
            hook,
            "Highly Advanced", "highly_advanced",
            CableTier.EV.casing,
            15, 1024_000L,
            SlotPositions.Builder().addSlots(26, 28, 5, 3).build(),
            SlotPositions.Builder().addSlot(134, 46).build(),
            176
        )
    }

    private fun registerMixedHatch(
        hook: SingleBlockSpecialMachinesMIHookContext,
        englishPrefix: String, prefix: String,
        casing: MachineCasing,
        itemSlotAmount: Int,
        fluidSlotCapacity: Long,
        itemPositions: SlotPositions,
        fluidPositions: SlotPositions,
        backgroundHeight: Int = 166
    ) {
        for (iter in 0..1) {
            val input = iter == 0
            val machine = prefix + "_mixed_" + (if (input) "input" else "output") + "_hatch"
            val englishName = englishPrefix + " Mixed" + (if (input) " Input" else " Output") + " Hatch"

            hook.builder(machine, englishName, { bep ->
                val itemSlots = List(itemSlotAmount) { if (input) ConfigurableItemStack.standardInputSlot() else ConfigurableItemStack.standardOutputSlot() }
                val fluidSlots = listOf(if (input) ConfigurableFluidStack.standardInputSlot(fluidSlotCapacity) else ConfigurableFluidStack.standardOutputSlot(fluidSlotCapacity))

                MixedHatch(
                    bep,
                    machine,
                    input,
                    prefix != "bronze",
                    MIInventory(itemSlots, fluidSlots, itemPositions, fluidPositions),
                    backgroundHeight
                )
            })
                .builtinModel(casing, "mixed_hatch", {
                    it
                        .front().side().top(false)
                        .active(false)
                        .outputTexture(YAI.id("block/overlays/output_mixed"))
                })
                .registrator({
                    MachineBlockEntity.registerItemApi(it)
                    MachineBlockEntity.registerFluidApi(it)
                })
                .registerMachine()
        }
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
                it.progressBar(76, 33, "extract")
            }).registerRecipeCategory()
            .registerMachine()

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

        hook.builder(PulseDetonationGeneratorBlockEntity.ID, PulseDetonationGeneratorBlockEntity.NAME, ::PulseDetonationGeneratorBlockEntity)
            .builtinModel(MachineCasings.TITANIUM_PIPE, PulseDetonationGeneratorBlockEntity.ID)
            .registerMultiblockShape(PulseDetonationGeneratorBlockEntity.SHAPE)
            .gui(SteamMode.NEITHER, RecipeTypes.PULSE_DETONATION_GENERATOR, {
                it.slots { slots ->
                    slots.itemInput(38, 35)
                    slots.fluidInput(56, 35)

                    slots.fluidOutput(102, 35)
                }
                it.progressBar(77, 33, "yai_explode")
            }).registerRecipeCategory()
            .registerMachine()

        hook.builder(LargeStorageUnitBlockEntity.ID, LargeStorageUnitBlockEntity.NAME, ::LargeStorageUnitBlockEntity)
            .builtinModel(Casings.BATTERY_ALLOY_MACHINE_CASING, LargeStorageUnitBlockEntity.ID)
            .registerMachine()

        hook.builder(NuclearRodIrradiatorBlockEntity.ID, NuclearRodIrradiatorBlockEntity.NAME, ::NuclearRodIrradiatorBlockEntity)
            .builtinModel(MachineCasings.NUCLEAR, NuclearRodIrradiatorBlockEntity.ID)
            .registerMachine()
    }

    object RecipeTypes {

        lateinit var CRYOGENIC_PRECIPITATOR: MachineRecipeType

        lateinit var ARBOREOUS_GREENHOUSE: MachineRecipeType

        lateinit var DRAGON_SIPHON: MachineRecipeType

        lateinit var PULSE_DETONATION_GENERATOR: MachineRecipeType

        val RECIPE_TYPES: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, YAI.ID)
        val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, YAI.ID)

        private val RECIPE_TYPE_NAMES = Maps.newHashMap<MachineRecipeType, String>()

        fun init(modBus: IEventBus) {
            RECIPE_TYPES.register(modBus)
            RECIPE_SERIALIZERS.register(modBus)

            MachineProcessConditions.register(YAI.id("arboreous_greenhouse_tier"), ArboreousGreenhouseTierCondition.CODEC, ArboreousGreenhouseTierCondition.STREAM_CODEC)
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
        ).withItemInputs().withItemOutputs().withFluidInputs()

        RecipeTypes.DRAGON_SIPHON = RecipeTypes.create(hook,
            DragonSiphonBlockEntity.NAME, DragonSiphonBlockEntity.ID
        ).withItemInputs().withFluidInputs().withFluidOutputs()

        RecipeTypes.PULSE_DETONATION_GENERATOR = RecipeTypes.create(hook,
            PulseDetonationGeneratorBlockEntity.NAME, PulseDetonationGeneratorBlockEntity.ID
        ).withItemInputs().withFluidInputs().withFluidOutputs()
    }

    object Casings {

        lateinit var STEEL_PLATED_END_STONE_BRICKS: MachineCasing
        lateinit var BATTERY_ALLOY_MACHINE_CASING: MachineCasing
        lateinit var DETONATION_CHAMBER_CASING: MachineCasing
        lateinit var CONFIGURABLE_MIXED_STORAGE: MachineCasing
    }

    fun machineCasings(hook: MachineCasingsMIHookContext) {
        Casings.STEEL_PLATED_END_STONE_BRICKS = hook.registerImitateBlock(
            YAIBlocks.SPESB_ID, YAIBlocks.STEEL_PLATED_END_STONE_BRICKS
        )

        Casings.BATTERY_ALLOY_MACHINE_CASING = hook.registerCubeAll(
            "battery_casing", "Battery Casing",
            YAI.id("block/battery_casing")
        )

        Casings.DETONATION_CHAMBER_CASING = hook.registerCubeAll(
            DetonationChamberCasingBlockEntity.ID, DetonationChamberCasingBlockEntity.NAME,
            YAI.id("block/${DetonationChamberCasingBlockEntity.ID}")
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

        hook.builder(ItemTrashCanHatch.ID, ItemTrashCanHatch.NAME)
            .special(::ItemTrashCanHatch)
            .builtinModel(MachineCasings.STEEL, ItemTrashCanHatch.ID)
            .registrator(MachineBlockEntity::registerItemApi)
            .registerMachine()

        hook.builder(FluidTrashCanHatch.ID, FluidTrashCanHatch.NAME)
            .special(::FluidTrashCanHatch)
            .builtinModel(MachineCasings.STEEL, FluidTrashCanHatch.ID)
            .registrator(MachineBlockEntity::registerFluidApi)
            .registerMachine()
    }

    fun getMachineFromId(id: String): Block {
        return YAIBlocks.Registry.BLOCKS.registry.get()
            .get(YAI.id(id)) ?: throw IllegalStateException("Failed to get YAI! machine with ID $id")
    }

}