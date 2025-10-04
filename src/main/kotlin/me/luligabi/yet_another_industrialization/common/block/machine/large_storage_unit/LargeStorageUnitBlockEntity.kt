package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.api.energy.CableTier
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.machines.multiblocks.*
import aztech.modern_industrialization.util.Tickable
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines.Casings
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper.Companion.GLASS_MEMBER
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedActiveShapeComponent
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes
import me.luligabi.yet_another_industrialization.common.misc.datamap.LargeStorageUnitTier
import me.luligabi.yet_another_industrialization.common.misc.material.YAIMaterials
import me.luligabi.yet_another_industrialization.mixin.EnergyComponentAccessor
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.swedz.tesseract.neoforge.compat.mi.material.part.MIMaterialParts
import java.util.*

class LargeStorageUnitBlockEntity(bep: BEP) : MultiblockMachineBlockEntity(
    bep,
    MachineGuiParameters.Builder(YAI.id(ID), false).backgroundHeight(156).build(),
    OrientationComponent.Params(false, false, false)
), Tickable {

    private val activeTier = SuppliedActiveShapeComponent({ SHAPE_TEMPLATES })
    private val chargingSlot = ChargingSlotComponent()

    val energy = EnergyComponent(this, { getTier().capacity })
    val insertable = energy.buildInsertable({ it.canTransferEu() })
    val extractable = energy.buildExtractable({ it.canTransferEu() })

    private var oldEu = 0L

    init {
        registerComponents(activeTier, chargingSlot, energy)

        registerGuiComponent(
            LargeStorageUnitGui.Server(
                { shapeValid.shapeValid },
                { (energy as EnergyComponentAccessor).storedEu }, { energy.capacity }
            )
        )

        registerGuiComponent(
            SuppliedShapeSelection(
                object : ShapeSelection.Behavior {

                    override fun handleClick(clickedLine: Int, delta: Int) {
                        activeTier.incrementShape(this@LargeStorageUnitBlockEntity, delta)
                    }

                    override fun getCurrentIndex(line: Int) = activeTier.activeShape
                },
                ::getTierInfo
            )
        )

        registerGuiComponent(ChargingSlot.Server(this, chargingSlot))
    }

    data class Tier(
        val blockId: ResourceLocation,
        val capacity: Long,
        val cableTier: CableTier,
        val translationKey: String
    ) {

        val blockMember = SimpleMember.forBlockId(blockId)
        val hullMember = SimpleMember.forBlockId(LargeStorageUnitTier.getHull(blockId, cableTier))

        fun getDisplayName() = Component.translatable(translationKey)
    }

    companion object {

        const val ID = "large_storage_unit"
        const val NAME = "Large Storage Unit"

        var TIERS = mutableListOf<Tier>()
            private set
        private var SHAPE_TEMPLATES = emptyArray<ShapeTemplate>()

        /**
         * _ -> air
         * # -> casing
         * @ -> glass
         * x -> tiered battery block
         * H -> tiered machine hull
         */
        private val BOTTOM_TOP_PATTERN = listOf(
            "#####",
            "#@@@#",
            "#@@@#",
            "#@@@#",
            "#####"
        )

        private val CORE_EDGE_PATTERN = listOf(
            "#@@@#",
            "@HxH@",
            "@xxx@",
            "@HxH@",
            "#@@@#"
        )

        private val CORE_MIDDLE_PATTERN = listOf(
            "#@@@#",
            "@xxx@",
            "@xxx@",
            "@xxx@",
            "#@@@#"
        )

        private val HATCHES = HatchFlags.Builder().with(
            YAIHatchTypes.LARGE_STORAGE_UNIT_INPUT,
            YAIHatchTypes.LARGE_STORAGE_UNIT_OUTPUT
        ).build()

        private val CASING_MEMBER = SimpleMember.forBlock { YAIMaterials.BATTERY_ALLOY.get(MIMaterialParts.MACHINE_CASING_SPECIAL).asBlock() }

        fun ShapeTemplate.Builder.addLayer(y: Int, pattern: List<String>, tier: Tier): ShapeTemplate.Builder {
            for (z in pattern.indices) {
                val row = pattern[z]
                for (x in row.indices) {
                    if (row[x] == '_') continue

                    val member = when (row[x]) {
                        '#' -> CASING_MEMBER
                        '@' -> GLASS_MEMBER
                        'x' -> tier.blockMember
                        'H' -> tier.hullMember
                        else -> continue
                    }
                    add(x - 2, y, z, member, if (member == CASING_MEMBER) HATCHES else null)
                }
            }
            return this
        }


        fun initTiers() {
            val registrationTiers = mutableListOf<Tier>()
            LargeStorageUnitTier.all().forEach { (block, tier) ->
                registrationTiers.add(tier.toRegisteredTier(block))
            }

            registrationTiers.sortBy { it.capacity }
            TIERS = Collections.unmodifiableList(registrationTiers)

            SHAPE_TEMPLATES = Array(TIERS.size) { i ->
                val tier = TIERS[i]

                ShapeTemplate.Builder(Casings.BATTERY_ALLOY_MACHINE_CASING)
                    .addLayer(-1, BOTTOM_TOP_PATTERN, tier)
                    .addLayer(0, CORE_EDGE_PATTERN, tier)
                    .addLayer(1, CORE_MIDDLE_PATTERN, tier)
                    .addLayer(2, CORE_EDGE_PATTERN, tier)
                    .addLayer(3, BOTTOM_TOP_PATTERN, tier)
                    .build()
            }


            ReiMachineRecipes.multiblockShapes.removeIf { it.machine() == YAI.id(ID) }
            SHAPE_TEMPLATES.forEachIndexed { i, shapeTemplate ->
                ReiMachineRecipes.registerMultiblockShape(YAI.id(ID), shapeTemplate, "$i")
            }
        }

    }

    override fun tick() {
        if (level?.isClientSide == true) return
        link()
        setChanged()
        if (energy.eu != oldEu) {
            oldEu = energy.eu
            sync(false)
        }

        chargingSlot.chargeItem(extractable)
    }

    override fun onRematch(shapeMatcher: ShapeMatcher) {
        if (!shapeMatcher.isMatchSuccessful) return

        invalidateCapabilities()
        shapeMatcher.matchedHatches.forEach {
            if (it is LargeStorageUnitHatch) it.setController(this)
        }
    }

    override fun getActiveShape() = SHAPE_TEMPLATES[activeTier.activeShape]

    override fun getBigShape() = SHAPE_TEMPLATES[0]

    override fun getInventory() = MIInventory.EMPTY

    override fun getMachineModelData(): MachineModelClientData {
        return MachineModelClientData(null, orientation.facingDirection).active(shapeValid.shapeValid)
    }

    private fun getTier() = TIERS[activeTier.activeShape]

    private fun getTierInfo(): ShapeSelection.LineInfo {
        return ShapeSelection.LineInfo(
            TIERS.size,
            TIERS.map { it.getDisplayName() }.toList(),
            true
        )
    }

    private fun CableTier.canTransferEu(): Boolean {
        return eu <= getTier().cableTier.eu
    }

}