package me.luligabi.yet_another_industrialization.common.block.machine.arboreous_greenhouse

import aztech.modern_industrialization.MI
import aztech.modern_industrialization.MIBlock
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractElectricCraftingMultiblockBlockEntity
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.components.OverdriveComponent
import aztech.modern_industrialization.machines.components.UpgradeComponent
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.init.MachineTier
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.multiblocks.HatchFlags
import aztech.modern_industrialization.machines.multiblocks.HatchTypes
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.machines.multiblocks.SimpleMember
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper.Companion.GLASS_MEMBER
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedActiveShapeComponent
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.yet_another_industrialization.common.misc.datamap.ArboreousGreenhouseTier.FluidByIdInput
import me.luligabi.yet_another_industrialization.mixin.AbstractCraftingMultiblockBlockEntityAccessor
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LanternBlock
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class ArboreousGreenhouseBlockEntity(bep: BEP) : AbstractElectricCraftingMultiblockBlockEntity(
    bep,
    ID,
    OrientationComponent.Params(false, false, false),
    SHAPE_TEMPLATES
), EnergyListComponentHolder {

    val activeSoil = SuppliedActiveShapeComponent({ SHAPE_TEMPLATES })
    val sapling = SaplingComponent()
    private val upgrades = UpgradeComponent()
    private val overdrive = OverdriveComponent()

    init {
        components.unregister(activeShape)
        registerComponents(activeSoil, sapling, upgrades, overdrive)

        registerGuiComponent(
            SlotPanel.Server(this)
                .withRedstoneControl(redstoneControl)
                .withUpgrades(upgrades)
                .withOverdrive(overdrive)
        )

        registerGuiComponent(
            SuppliedShapeSelection(
                object : ShapeSelection.Behavior {

                    override fun handleClick(clickedLine: Int, delta: Int) {
                        activeSoil.incrementShape(this@ArboreousGreenhouseBlockEntity, delta)
                    }

                    override fun getCurrentIndex(line: Int) = activeSoil.activeShape

                },
                ::getSoilInfo
            )
        )
    }

    data class Tier(
        val id: ResourceLocation,
        val iconId: ResourceLocation,
        val translationKey: String,
        val fluid: Optional<FluidByIdInput>,
        val nutrientFluid: Optional<FluidByIdInput>,
        val sortOrder: Int
    ) {

        val blockState = BuiltInRegistries.BLOCK.get(ResourceKey.create(Registries.BLOCK, iconId))?.defaultBlockState()
        val validSoils = mutableSetOf<Block>()

        fun getDisplayName() = Component.translatable(translationKey)
    }

    companion object {
        const val ID = "arboreous_greenhouse"
        const val NAME = "Arboreous Greenhouse"

        var TIERS = mutableListOf<Tier>()
            private set
        private var SHAPE_TEMPLATES = emptyArray<ShapeTemplate>()

        /**
         * _ -> air
         * # -> casing
         * $ -> soil
         * @ -> glass
         * x -> lantern
         */
        private val BASE_PATTERN = listOf(
            "__#####__",
            "_#$$$$$#_",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "#$$$$$$$#",
            "_#$$$$$#_",
            "__#####__"
        )

        private val DOME_1_PATTERN = listOf(
            "__@@@@@__",
            "_#_____#_",
            "@_______@",
            "@_______@",
            "@_______@",
            "@_______@",
            "@_______@",
            "_#_____#_",
            "__@@@@@__"
        )

        private val DOME_2_PATTERN = listOf(
            "___@@@___",
            "__#___#__",
            "_#_____#_",
            "@_______@",
            "@_______@",
            "@_______@",
            "_#_____#_",
            "__#___#__",
            "___@@@___"
        )

        private val DOME_3_PATTERN = listOf(
            "_________",
            "___###___",
            "__#___#__",
            "_#_____#_",
            "_#_____#_",
            "_#_____#_",
            "__#___#__",
            "___###___",
            "_________"
        )

        private val DOME_4_PATTERN = listOf(
            "_________",
            "_________",
            "___@@@___",
            "__@___@__",
            "__@_x_@__",
            "__@___@__",
            "___@@@___",
            "_________",
            "_________"
        )

        private val DOME_5_PATTERN = listOf(
            "_________",
            "_________",
            "_________",
            "___@@@___",
            "___@#@___",
            "___@@@___",
            "_________",
            "_________",
            "_________"
        )

        private val HANGING_LANTERN_MEMBER = object : SimpleMember {

            override fun matchesState(state: BlockState) =
                state.`is`(Blocks.LANTERN) && state.getValue(LanternBlock.HANGING)

            override fun getPreviewState() = Blocks.LANTERN.defaultBlockState()
                .setValue(LanternBlock.HANGING, true)

        }

        private val HATCHES = HatchFlags.Builder().with(
            HatchTypes.ITEM_INPUT, HatchTypes.ITEM_OUTPUT,
            HatchTypes.FLUID_INPUT,
            HatchTypes.ENERGY_INPUT
        ).build()


        fun ShapeTemplate.Builder.addLayer(y: Int, pattern: List<String>, soil: SimpleMember): ShapeTemplate.Builder {
            val casing = SimpleMember.forBlock(MIBlock.BLOCK_DEFINITIONS[MI.id("heatproof_machine_casing")])

            for (z in pattern.indices) {
                val row = pattern[z]
                for (x in row.indices) {
                    if (row[x] == '_') continue

                    val member = when (row[x]) {
                        '#' -> casing
                        '$' -> soil
                        '@' -> GLASS_MEMBER
                        'x' -> HANGING_LANTERN_MEMBER
                        else -> continue
                    }
                    add(x - 4, y, z, member, if (member == casing) HATCHES else null)
                }
            }

            return this
        }


        fun initTiers() {
            val registrationTiers = mutableListOf<Tier>()

            ArboreousGreenhouseTier.all().forEach { (block, tier) ->
                if (registrationTiers.map { it.id }.contains(tier.id)) {
                    BuiltInRegistries.BLOCK.get(block)?.let { block ->
                        registrationTiers.find { it.id == tier.id }!!.validSoils.add(block)
                    }
                    return@forEach
                }

                val registeredTier = tier.toRegisteredTier()
                BuiltInRegistries.BLOCK.get(block)?.let { registeredTier.validSoils.add(it) }
                registrationTiers.add(registeredTier)
            }

            registrationTiers.sortBy { it.sortOrder }
            TIERS = Collections.unmodifiableList(registrationTiers)

            SHAPE_TEMPLATES = Array(TIERS.size) { i ->
                val tier = TIERS[i]
                val soilBlocks = TierSimpleMember(tier)

                ShapeTemplate.Builder(MachineCasings.HEATPROOF)
                    .addLayer(0, BASE_PATTERN, soilBlocks)
                    .addLayer(1, DOME_1_PATTERN, soilBlocks)
                    .addLayer(2, DOME_1_PATTERN, soilBlocks)
                    .addLayer(3, DOME_1_PATTERN, soilBlocks)
                    .addLayer(4, DOME_2_PATTERN, soilBlocks)
                    .addLayer(5, DOME_3_PATTERN, soilBlocks)
                    .addLayer(6, DOME_4_PATTERN, soilBlocks)
                    .addLayer(7, DOME_5_PATTERN, soilBlocks)
                    .build()
            }
            for (i in SHAPE_TEMPLATES.indices) {
                val alternativeId = "${TIERS[i].id.namespace}/${TIERS[i].id.path}"
                if (ReiMachineRecipes.multiblockShapes.any { it.alternative == alternativeId }) continue

                ReiMachineRecipes.registerMultiblockShape(YAI.id(ID), SHAPE_TEMPLATES[i], alternativeId)
            }

            (YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE as ArboreousGreenhouseRecipeType).clearCache()
        }

    }

    private var oldActive = false
    override fun tickExtra() {
        if (level?.isClientSide == true) return
        if (level!!.gameTime % 20L != 0L) return

        val newActive = (this as AbstractCraftingMultiblockBlockEntityAccessor).isActive.isActive
        if (oldActive != newActive) {
            if (newActive) {
                sapling.update(crafter, this)
            } else {
                sapling.reset(this)
            }
        }
        oldActive = newActive
    }

    override fun onCraft() {
        sapling.update(crafter, this)
    }

    override fun recipeType() = YAIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE

    override fun getBaseRecipeEu() = MachineTier.MULTIBLOCK.baseEu.toLong()

    override fun getMaxRecipeEu() = MachineTier.MULTIBLOCK.maxEu + upgrades.addMaxEUPerTick

    override fun isOverdriving() = overdrive.shouldOverdrive()

    override fun getActiveShape() = SHAPE_TEMPLATES[activeSoil.activeShape]

    override fun getBigShape() = SHAPE_TEMPLATES[0]

    private fun getSoilInfo(): ShapeSelection.LineInfo {
        return ShapeSelection.LineInfo(
            TIERS.size,
            TIERS.map { it.getDisplayName() }.toList(),
            true
        )
    }

    private class TierSimpleMember(private val tier: Tier) : SimpleMember {

        override fun matchesState(state: BlockState) = state.block in tier.validSoils

        override fun getPreviewState() = tier.blockState ?: tier.validSoils.firstOrNull()?.defaultBlockState()
    }

}