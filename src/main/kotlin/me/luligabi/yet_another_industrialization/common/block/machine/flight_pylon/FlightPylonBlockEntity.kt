package me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.EnergyComponent
import aztech.modern_industrialization.machines.components.IsActiveComponent
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.components.RedstoneControlComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.ShapeSelection
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.machines.multiblocks.*
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import aztech.modern_industrialization.util.Simulation
import aztech.modern_industrialization.util.TextHelper
import aztech.modern_industrialization.util.Tickable
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedActiveShapeComponent
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.SuppliedShapeSelection
import me.luligabi.yet_another_industrialization.common.block.machine.util.components.ToggleCheckbox
import me.luligabi.yet_another_industrialization.common.misc.datamap.FlightPylonTier
import me.luligabi.yet_another_industrialization.common.misc.effect.YAIEffects
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.RED
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.WHITE
import java.util.*

class FlightPylonBlockEntity(bep: BEP): MultiblockMachineBlockEntity(
    bep,
    MachineGuiParameters.Builder(YAI.id(ID), false).backgroundHeight(156).build(),
    OrientationComponent.Params(false, false, false)
), Tickable, EnergyListComponentHolder {

    private val activeTier = SuppliedActiveShapeComponent({ SHAPE_TEMPLATES })
    private val redstoneControl = RedstoneControlComponent()
    val isActive = IsActiveComponent()
    val beaconComponent = FlightPylonBeaconComponent()

    private val energyInputs = mutableListOf<EnergyComponent>()

    init {
        registerComponents(activeTier, isActive, redstoneControl, beaconComponent)

        registerGuiComponent(
            SlotPanel(this)
                .withRedstoneControl(redstoneControl)
        )

        registerGuiComponent(
            ModularMultiblockGui(
                40,
                { gui ->
                    gui.add(statusComponent(), if (isShapeValid) WHITE else RED)
                    if (!isActive.isActive) return@ModularMultiblockGui
                    getTier().let {
                        gui.add(YAI.TEXT.flightPylonRange(it.range.toInt()))
                        gui.add(YAI.TEXT.flightPylonEnergy(TextHelper.getEuTextTick(it.eu)))
                    }
                },
                { emptyList() }
            ))

        registerGuiComponent(
            SuppliedShapeSelection(
                object : ShapeSelection.Behavior {

                    override fun handleClick(clickedLine: Int, delta: Int) {
                        activeTier.incrementShape(this@FlightPylonBlockEntity, delta)
                    }

                    override fun getCurrentIndex(line: Int) = activeTier.activeShape
                },
                ::getTierInfo
            )
        )

        registerGuiComponent(
            ToggleCheckbox(
                {
                    beaconComponent.enabled = it
                    sync()
                },
                { beaconComponent.enabled },
                listOf(YAI.TEXT.flightPylonBeaconToggle())
            )
        )
    }

    data class Tier(
        val blockId: ResourceLocation,
        val range: Double,
        val eu: Long,
        val translationKey: String,
        val beaconColor: Int
    ) {

        val blockMember = SimpleMember.forBlockId(blockId)

        fun getDisplayName() = Component.translatable(translationKey)
    }

    companion object {

        const val ID = "flight_pylon"
        const val NAME = "Flight Pylon"

        var TIERS = mutableListOf<Tier>()
            private set
        private var SHAPE_TEMPLATES = emptyArray<ShapeTemplate>()

        private val LAYER_0 = listOf(
            "_____",
            "_###_",
            "_###_",
            "_###_",
            "_____"
        )

        private val LAYER_1 = listOf(
            "_____",
            "__@__",
            "_@@@_",
            "__@__",
            "_____"
        )

        private val LAYER_2 = listOf(
            "__@__",
            "_____",
            "@___@",
            "_____",
            "__@__"
        )

        private val LAYER_3 = listOf(
            "_____",
            "_@_@_",
            "_____",
            "_@_@_",
            "_____"
        )

        private val LAYER_4 = listOf(
            "_____",
            "_@_@_",
            "__@__",
            "_@_@_",
            "_____"
        )

        private val LAYER_5 = listOf(
            "_____",
            "_____",
            "__@__",
            "_____",
            "_____"
        )

        private val HATCHES = HatchFlags.Builder().with(
            HatchTypes.ENERGY_INPUT
        ).build()

        private val CASING_MEMBER = SimpleMember.forBlock { MIMaterials.STEEL.getPart(MIParts.MACHINE_CASING).asBlock() }

        fun ShapeTemplate.Builder.addLayer(y: Int, pattern: List<String>, tier: Tier): ShapeTemplate.Builder {
            for (z in pattern.indices) {
                val row = pattern[z]
                for (x in row.indices) {
                    if (row[x] == '_') continue

                    val member = when (row[x]) {
                        '#' -> CASING_MEMBER
                        '@' -> tier.blockMember
                        else -> continue
                    }
                    add(x - 2, y, z - 1, member, if (member == CASING_MEMBER) HATCHES else null)
                }
            }
            return this
        }

        fun initTiers() {
            val registrationTiers = mutableListOf<Tier>()
            FlightPylonTier.all().forEach { (block, tier) ->
                registrationTiers.add(tier.toRegisteredTier(block))
            }

            registrationTiers.sortBy { it.range }
            TIERS = Collections.unmodifiableList(registrationTiers)

            SHAPE_TEMPLATES = Array(TIERS.size) { i ->
                val tier = TIERS[i]

                ShapeTemplate.Builder(MachineCasings.STEEL)
                    .addLayer(0, LAYER_0, tier)
                    .addLayer(1, LAYER_1, tier)
                    .addLayer(2, LAYER_2, tier)
                    .addLayer(3, LAYER_3, tier)
                    .addLayer(4, LAYER_3, tier)
                    .addLayer(5, LAYER_4, tier)
                    .addLayer(6, LAYER_5, tier)
                    .addLayer(7, LAYER_5, tier)
                    .addLayer(8, LAYER_5, tier)
                    .build()
            }


            ReiMachineRecipes.multiblockShapes.removeIf { it.machine() == YAI.id(ID) }
            SHAPE_TEMPLATES.forEachIndexed { i, shapeTemplate ->
                ReiMachineRecipes.registerMultiblockShape(YAI.id(ID), shapeTemplate, "$i")
            }
        }

    }

    override fun tick() {
        if (level!!.isClientSide) return
        link()

        if (!shapeValid.shapeValid || !redstoneControl.doAllowNormalOperation(this)) {
            isActive.updateActive(false, this)
            return
        }

        var newActive = false
        if (consumeEu(Simulation.SIMULATE)) {
            if (level!!.gameTime % (6 * 20L) == 0L) {
                applyEffect()
            }
            consumeEu(Simulation.ACT)
            newActive = true
        }
        isActive.updateActive(newActive, this)
    }

    override fun onRematch(shapeMatcher: ShapeMatcher) {
        if (!shapeMatcher.isMatchSuccessful) return
        energyInputs.clear()

        for (hatch in shapeMatcher.matchedHatches) {
            hatch.appendEnergyInputs(energyInputs)
        }
    }

    override fun useItemOn(player: Player, hand: InteractionHand, face: Direction): ItemInteractionResult {
        var result = super.useItemOn(player, hand, face)
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand)
        }
        return result
    }

    override fun getInventory() = MIInventory.EMPTY

    override fun getEnergyComponents() = energyInputs

    override fun getActiveShape() = SHAPE_TEMPLATES[activeTier.activeShape]

    override fun getMachineModelData(): MachineModelClientData {
        return MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive)
    }

    fun getTier() = TIERS[activeTier.activeShape]

    private fun getTierInfo(): ShapeSelection.LineInfo {
        return ShapeSelection.LineInfo(
            TIERS.map { it.getDisplayName() }.toList(),
            true
        )
    }

    private fun consumeEu(simulation: Simulation): Boolean {
        val toConsume = getTier().eu
        var total = 0L

        for (energyComponent in energyInputs) {
            total += energyComponent.consumeEu(toConsume - total, simulation)
        }

        return total == toConsume
    }

    private fun applyEffect() {
        val box = AABB(this.blockPos.relative(this.orientation.facingDirection.opposite))
            .inflate(getTier().range)
            .expandTowards(0.0, level!!.height.toDouble(), 0.0)
        val list = level!!.getEntitiesOfClass(Player::class.java, box)
        for (player in list) {
            player.addEffect(MobEffectInstance(YAIEffects.CREATIVE_FLIGHT, 8 * 20, 0, true, true))
        }
    }

    private fun statusComponent(): Component {
        if (!isShapeValid) return MIText.MultiblockShapeInvalid.text()

        return (if (isActive.isActive) MIText.MultiblockStatusActive else MIText.MultiblockShapeValid).text()
    }

}