package me.luligabi.hostile_neural_industrialization.common.block.machine

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.compat.rei.machines.MachineCategoryParams
import aztech.modern_industrialization.compat.rei.machines.SteamMode
import aztech.modern_industrialization.inventory.SlotPositions
import aztech.modern_industrialization.machines.guicomponents.ProgressBar
import aztech.modern_industrialization.machines.init.MultiblockMachines.Rei
import aztech.modern_industrialization.machines.init.SingleBlockCraftingMachines
import aztech.modern_industrialization.machines.models.MachineCasing
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.recipe.MachineRecipeType
import com.google.common.collect.Maps
import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.block.HNIBlocks
import me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse.ArboreousGreenhouseRecipeType
import me.luligabi.hostile_neural_industrialization.common.block.machine.chunky_tank.ChunkyTankHatch
import me.luligabi.hostile_neural_industrialization.common.block.machine.chunky_tank.ChunkyTankMultiblockBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.crafter.CrafterBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.crafter.CrafterRecipeType
import me.luligabi.hostile_neural_industrialization.common.block.machine.dragon_siphon.DragonSiphonBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.dragon_siphon.DragonSiphonRecipeType
import me.luligabi.hostile_neural_industrialization.common.block.machine.loot_fabricator.large.LargeLootFabricatorBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.loot_fabricator.large.LargeLootFabricatorRecipeType
import me.luligabi.hostile_neural_industrialization.common.block.machine.loot_fabricator.mono.MonoLootFabricatorBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.loot_fabricator.mono.MonoLootFabricatorRecipeType
import me.luligabi.hostile_neural_industrialization.common.block.machine.sim_chamber.electric.ElectricSimChamberBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.sim_chamber.electric.ElectricSimChamberRecipeType
import me.luligabi.hostile_neural_industrialization.common.block.machine.sim_chamber.large.LargeSimChamberBlockEntity
import me.luligabi.hostile_neural_industrialization.common.block.machine.sim_chamber.large.LargeSimChamberRecipeType
import me.luligabi.hostile_neural_industrialization.common.item.HNIItems
import me.luligabi.hostile_neural_industrialization.mixin.HackedMachineRegistrationHelperAccessor
import me.luligabi.hostile_neural_industrialization.mixin.MIHookContextAccessor
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookTracker
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.*

object HNIMachines {

    fun singleBlockCrafting(hook: SingleBlockCraftingMachinesMIHookContext) {
//        hook.register(
//            "Crafter", "crafter",
//            RecipeTypes.CRAFTER,
//            9, 1, 1, 0,
//            { it.backgroundHeight(186) },
//            ProgressBar.Parameters(105, 42, "arrow"),
//            RecipeEfficiencyBar.Parameters(42, 86),
//            EnergyBar.Parameters(14, 44),
//            { it.addSlots(51, 27, 3, 3).addSlot(131, 45)},
//            { it.addSlot(33, 27)},
//            true, true, false,
//            SingleBlockCraftingMachines.TIER_STEEL or SingleBlockCraftingMachines.TIER_ELECTRIC,
//            16
//        )
    }

    fun singleBlockSpecial(hook: SingleBlockSpecialMachinesMIHookContext) {
        hook.register(
            CrafterBlockEntity.NAME, CrafterBlockEntity.ID, CrafterBlockEntity.ID,
            CableTier.LV.casing, true, false, false, true,
            ::CrafterBlockEntity,
            CrafterBlockEntity::registerEnergyApi
        )

        hook.register(
            ChunkyTankHatch.NAME, ChunkyTankHatch.ID, ChunkyTankHatch.ID,
            CableTier.LV.casing, true, false, false, true,
            ::ChunkyTankHatch,
            ChunkyTankHatch::registerFluidApi
        )







        hook.register(
            ElectricSimChamberBlockEntity.NAME, ElectricSimChamberBlockEntity.ID, ElectricSimChamberBlockEntity.ID,
            CableTier.LV.casing, true, false, false, true,
            ::ElectricSimChamberBlockEntity,
            ElectricSimChamberBlockEntity::registerEnergyApi
        )
        HackedMachineRegistrationHelperAccessor.invokeRegisterReiTiers(
            (hook as MIHookContextAccessor).hook,
            ElectricSimChamberBlockEntity.NAME, ElectricSimChamberBlockEntity.ID,
            RecipeTypes.ELECTRIC_SIM_CHAMBER,
            MachineCategoryParams(
                null, null,
                SlotPositions.Builder().addSlots(57, 27, 1, 2).build(),
                SlotPositions.Builder().addSlots(103, 27, 1, 2).build(),
                SlotPositions.Builder().addSlot(39, 27).build(),
                SlotPositions.Builder().addSlot(121, 27).build(),
                ProgressBar.Parameters(79, 34, "compress"),
                RecipeTypes.ELECTRIC_SIM_CHAMBER,
                null,
                false,
                SteamMode.ELECTRIC_ONLY
            ),
            SingleBlockCraftingMachines.TIER_ELECTRIC
        )

        hook.register(
            MonoLootFabricatorBlockEntity.NAME, MonoLootFabricatorBlockEntity.ID, MonoLootFabricatorBlockEntity.ID,
            CableTier.LV.casing, true, false, false, true,
            ::MonoLootFabricatorBlockEntity,
            MonoLootFabricatorBlockEntity::registerEnergyApi
        )
        HackedMachineRegistrationHelperAccessor.invokeRegisterReiTiers(
            (hook as MIHookContextAccessor).hook,
            MonoLootFabricatorBlockEntity.NAME, MonoLootFabricatorBlockEntity.ID,
            RecipeTypes.MONO_LOOT_FABRICATOR,
            MachineCategoryParams(
                null, null,
                SlotPositions.Builder().addSlot(56, 39).build(),
                SlotPositions.Builder().addSlot(102, 39).build(),
                SlotPositions.Builder().addSlot(56, 57).build(),
                SlotPositions.Builder().addSlot(102, 57).build(),
                ProgressBar.Parameters(78, 43, "compress"),
                RecipeTypes.MONO_LOOT_FABRICATOR,
                null,
                false,
                SteamMode.ELECTRIC_ONLY
            ),
            SingleBlockCraftingMachines.TIER_ELECTRIC
        )

    }

    fun multiblockMachines(hook: MultiblockMachinesMIHookContext) {
        hook.register(
            ArboreousGreenhouseBlockEntity.NAME, ArboreousGreenhouseBlockEntity.ID, ArboreousGreenhouseBlockEntity.ID,
            MachineCasings.HEATPROOF, true, false, false, true,
            ::ArboreousGreenhouseBlockEntity
        )
        Rei(ArboreousGreenhouseBlockEntity.NAME, HNI.id(ArboreousGreenhouseBlockEntity.ID), RecipeTypes.ARBOREOUS_GREENHOUSE, ProgressBar.Parameters(77, 34, "extract"))
            .items(
                { it.addSlot(56, 26) },
                { it.addSlots(102, 26, 2, 2) }
            )
            .fluids(
                { it.addSlot(56, 44) },
                {}
            )
            .workstations(HNI.id(ArboreousGreenhouseBlockEntity.ID))
            .register()
        MIHookTracker.addReiCategoryName(HNI.id(ArboreousGreenhouseBlockEntity.ID), ArboreousGreenhouseBlockEntity.NAME)


        hook.register(
            DragonSiphonBlockEntity.NAME, DragonSiphonBlockEntity.ID, DragonSiphonBlockEntity.ID,
            MachineCasings.SOLID_TITANIUM, true, false, false, true,
            ::DragonSiphonBlockEntity,
            { DragonSiphonBlockEntity.registerReiShapes() }
        )
        Rei(DragonSiphonBlockEntity.NAME, HNI.id(DragonSiphonBlockEntity.ID), RecipeTypes.DRAGON_SIPHON, ProgressBar.Parameters(88, 31, "arrow"))
            .items(
                { it.addSlot(40, 35) },
                {}
            )
            .fluids(
                { it.addSlot(60, 35) },
                { it.addSlot(120, 35) }
            )
            .workstations(HNI.id(DragonSiphonBlockEntity.ID))
            .register()
        MIHookTracker.addReiCategoryName(HNI.id(DragonSiphonBlockEntity.ID), DragonSiphonBlockEntity.NAME)

        hook.register(
            ChunkyTankMultiblockBlockEntity.NAME, ChunkyTankMultiblockBlockEntity.ID, ChunkyTankMultiblockBlockEntity.ID,
            MachineCasings.CLEAN_STAINLESS_STEEL, true, false, true,
            ::ChunkyTankMultiblockBlockEntity,
            { ChunkyTankMultiblockBlockEntity.registerReiShapes() }
        )












        hook.register(
            LargeSimChamberBlockEntity.NAME, LargeSimChamberBlockEntity.ID, LargeSimChamberBlockEntity.ID,
            Casings.PREDICTION_MACHINE_CASING, true, false, false, true,
            ::LargeSimChamberBlockEntity,
            { LargeSimChamberBlockEntity.registerReiShapes() }
        )
        Rei(LargeSimChamberBlockEntity.NAME, HNI.id(LargeSimChamberBlockEntity.ID), RecipeTypes.LARGE_SIM_CHAMBER, ProgressBar.Parameters(77, 33, "compress"))
            .items(
                { it.addSlots(58, 27, 1, 2) },
                { it.addSlots(102, 27, 1, 2) }
            )
            .fluids(
                { it.addSlot(40, 27) },
                { it.addSlot(120, 27) }
            )
            .workstations(HNI.id(LargeSimChamberBlockEntity.ID))
            .register()
        MIHookTracker.addReiCategoryName(HNI.id(LargeSimChamberBlockEntity.ID), LargeSimChamberBlockEntity.NAME)

        hook.register(
            LargeLootFabricatorBlockEntity.NAME, LargeLootFabricatorBlockEntity.ID, LargeLootFabricatorBlockEntity.ID,
            Casings.PREDICTION_MACHINE_CASING, true, false, false, true,
            ::LargeLootFabricatorBlockEntity,
            { LargeLootFabricatorBlockEntity.registerReiShapes() }
        )
        Rei(LargeLootFabricatorBlockEntity.NAME, HNI.id(LargeLootFabricatorBlockEntity.ID), RecipeTypes.LARGE_LOOT_FABRICATOR, ProgressBar.Parameters(77, 33, "compress"))
            .items(
                { it.addSlot(56, 35) },
                { it.addSlots(102, 35, 5, 4) }
            )
            .fluids(
                { it.addSlot(56, 53) },
                { it.addSlot(84, 89) }
            )
            .workstations(HNI.id(LargeLootFabricatorBlockEntity.ID))
            .register()
        MIHookTracker.addReiCategoryName(HNI.id(LargeLootFabricatorBlockEntity.ID), LargeLootFabricatorBlockEntity.NAME)

    }

    object RecipeTypes {

        lateinit var ARBOREOUS_GREENHOUSE: MachineRecipeType
        lateinit var CRAFTER: MachineRecipeType

        lateinit var DRAGON_SIPHON: MachineRecipeType


        lateinit var ELECTRIC_SIM_CHAMBER: MachineRecipeType
        lateinit var LARGE_SIM_CHAMBER: MachineRecipeType

        lateinit var MONO_LOOT_FABRICATOR: MachineRecipeType
        lateinit var LARGE_LOOT_FABRICATOR: MachineRecipeType


        val RECIPE_TYPES: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, HNI.ID)
        val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(Registries.RECIPE_SERIALIZER, HNI.ID)

        private val RECIPE_TYPE_NAMES = Maps.newHashMap<MachineRecipeType, String>()

        fun init(modBus: IEventBus) {
            RECIPE_TYPES.register(modBus)
            RECIPE_SERIALIZERS.register(modBus)
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
        RecipeTypes.ARBOREOUS_GREENHOUSE = RecipeTypes.create(hook,
            ArboreousGreenhouseBlockEntity.NAME, ArboreousGreenhouseBlockEntity.ID,
            ::ArboreousGreenhouseRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs()

        RecipeTypes.CRAFTER = RecipeTypes.create(hook,
            CrafterBlockEntity.NAME, CrafterBlockEntity.ID,
            ::CrafterRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs()

        RecipeTypes.DRAGON_SIPHON = RecipeTypes.create(hook,
            DragonSiphonBlockEntity.NAME, DragonSiphonBlockEntity.ID,
            ::DragonSiphonRecipeType
        ).withItemInputs().withFluidInputs().withFluidOutputs()



        RecipeTypes.ELECTRIC_SIM_CHAMBER = RecipeTypes.create(hook,
            ElectricSimChamberBlockEntity.NAME, ElectricSimChamberBlockEntity.ID,
            ::ElectricSimChamberRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs().withFluidOutputs()

        RecipeTypes.LARGE_SIM_CHAMBER = RecipeTypes.create(hook,
            LargeSimChamberBlockEntity.NAME, LargeSimChamberBlockEntity.ID,
            ::LargeSimChamberRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs().withFluidOutputs()

        RecipeTypes.MONO_LOOT_FABRICATOR = RecipeTypes.create(hook,
            MonoLootFabricatorBlockEntity.NAME, MonoLootFabricatorBlockEntity.ID,
            ::MonoLootFabricatorRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs().withFluidOutputs()

        RecipeTypes.LARGE_LOOT_FABRICATOR = RecipeTypes.create(hook,
            LargeLootFabricatorBlockEntity.NAME, LargeLootFabricatorBlockEntity.ID,
            ::LargeLootFabricatorRecipeType
        ).withItemInputs().withItemOutputs().withFluidInputs().withFluidOutputs()
    }

    object Casings {

        lateinit var PREDICTION_MACHINE_CASING: MachineCasing
    }

    fun machineCasings(hook: MachineCasingsMIHookContext) {

        Casings.PREDICTION_MACHINE_CASING = hook.registerImitateBlock("prediction_machine_casing") {
            HNIBlocks.PREDICTION_MACHINE_CASING.get()
        }

    }


    fun getMachineFromId(id: String): Item {
        return HNIItems.Registry.ITEMS.registry.get()
            .get(HNI.id(id)) ?: throw IllegalStateException("Failed to get HNI machine with ID $id")
    }

}