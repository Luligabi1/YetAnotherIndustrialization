package me.luligabi.hostile_neural_industrialization.common.block.machine.arboreous_greenhouse

import aztech.modern_industrialization.MI
import aztech.modern_industrialization.MIBlock
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.IComponent
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractElectricCraftingMultiblockBlockEntity
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.components.OverdriveComponent
import aztech.modern_industrialization.machines.components.UpgradeComponent
import aztech.modern_industrialization.machines.gui.GuiComponent
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.init.MachineTier
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.multiblocks.*
import me.luligabi.hostile_neural_industrialization.common.HNI
import me.luligabi.hostile_neural_industrialization.common.block.machine.HNIMachines
import me.luligabi.hostile_neural_industrialization.common.block.machine.HNIMultiblockHelper.Companion.GLASS_MEMBER
import me.luligabi.hostile_neural_industrialization.common.misc.datamap.ArboreousGreenhouseTier
import me.luligabi.hostile_neural_industrialization.common.misc.datamap.ArboreousGreenhouseTier.FluidByIdInput
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LanternBlock
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.stream.IntStream

class ArboreousGreenhouseBlockEntity(bep: BEP) : AbstractElectricCraftingMultiblockBlockEntity(
    bep,
    ID,
    OrientationComponent.Params(false, false, false),
    SHAPE_TEMPLATES
), EnergyListComponentHolder {

    val activeSoil = ActiveSoilComponent { SHAPE_TEMPLATES }
    private val upgrades = UpgradeComponent()
    private val overdrive = OverdriveComponent()

    init {
        components.unregister(activeShape)
        registerComponents(activeSoil, upgrades, overdrive)

        registerGuiComponent(
            SlotPanel.Server(this)
                .withRedstoneControl(redstoneControl)
                .withUpgrades(upgrades)
                .withOverdrive(overdrive)
        )

        registerGuiComponent(
            SoilSelection(
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
            HatchType.ITEM_INPUT, HatchType.ITEM_OUTPUT,
            HatchType.FLUID_INPUT,
            HatchType.ENERGY_INPUT
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

                ReiMachineRecipes.registerMultiblockShape(HNI.id(ID), SHAPE_TEMPLATES[i], alternativeId)
            }

            (HNIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE as ArboreousGreenhouseRecipeType).clearCache()
        }

    }

    override fun recipeType() = HNIMachines.RecipeTypes.ARBOREOUS_GREENHOUSE

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
    
    class SoilSelection(
        val behavior: ShapeSelection.Behavior,
        private vararg val lines: () -> ShapeSelection.LineInfo
    ): GuiComponent.Server<IntArray> {

        override fun copyData(): IntArray {
            return IntStream.range(0, lines.size)
                .map { line: Int -> behavior.getCurrentIndex(line) }.toArray()
        }

        override fun needsSync(cachedData: IntArray): Boolean {
            for (i in lines.indices) {
                if (cachedData[i] != behavior.getCurrentIndex(i)) {
                    return true
                }
            }
            return false
        }

        override fun writeInitialData(buf: RegistryFriendlyByteBuf) {
            buf.writeVarInt(lines.size)
            for (line in lines) {
                val currentLine = line()
                buf.writeVarInt(currentLine.numValues)
                for (component in currentLine.translations) {
                    ComponentSerialization.STREAM_CODEC.encode(buf, component)
                }
                buf.writeBoolean(currentLine.useArrows)
            }
            writeCurrentData(buf)
        }

        override fun writeCurrentData(buf: RegistryFriendlyByteBuf) {
            for (i in lines.indices) {
                buf.writeVarInt(behavior.getCurrentIndex(i))
            }
        }

        override fun getId() = ID

        companion object {
            val ID = HNI.id("soil_selection")
        }
        
    }

    // ActiveShapeComponent but using a Supplier - if the available shapes change, the regular component could cause AIOOB Exceptions
    class ActiveSoilComponent(private val shapeTemplates: () -> Array<ShapeTemplate>): IComponent {

        var activeShape = 0
            private set

        fun incrementShape(machine: MultiblockMachineBlockEntity, delta: Int) {
            val newShape = activeShape + delta
            val capped = newShape.coerceIn(0, shapeTemplates().size - 1)
            setShape(machine, capped)
        }

        fun setShape(machine: MultiblockMachineBlockEntity, newShape: Int) {
            if (newShape != activeShape) {
                activeShape = newShape
                machine.setChanged()
                machine.unlink()
                machine.sync(false)
            }
        }

        override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider?) {
            tag.putInt("activeShape", activeShape)
        }

        override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider?, isUpgradingMachine: Boolean) {
            activeShape = tag.getInt("activeShape").coerceAtMost(shapeTemplates().size - 1)
        }

    }

}