package me.luligabi.yet_another_industrialization.common.block.machine.nuclear_rod_irradiator

import aztech.modern_industrialization.MIText
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.api.machine.holder.MultiblockInventoryComponentHolder
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import aztech.modern_industrialization.machines.components.*
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.models.MachineCasings
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.machines.multiblocks.*
import aztech.modern_industrialization.materials.MIMaterials
import aztech.modern_industrialization.materials.part.MIParts
import aztech.modern_industrialization.nuclear.NuclearAbsorbable
import aztech.modern_industrialization.nuclear.NuclearFuel
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction
import aztech.modern_industrialization.util.Simulation
import aztech.modern_industrialization.util.TextHelper
import aztech.modern_industrialization.util.Tickable
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMultiblockHelper
import me.luligabi.yet_another_industrialization.common.misc.datamap.IrradiatorNeutronSource
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGui
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.RED
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.modularmultiblock.ModularMultiblockGuiLine.WHITE
import java.util.concurrent.ThreadLocalRandom
import kotlin.jvm.optionals.getOrNull

class NuclearRodIrradiatorBlockEntity(bep: BEP): MultiblockMachineBlockEntity(
    bep,
    MachineGuiParameters.Builder(YAI.id(ID), false).backgroundHeight(156).build(),
    OrientationComponent.Params(false, false, false)
), Tickable, MultiblockInventoryComponentHolder, EnergyListComponentHolder {

    companion object : YAIMultiblockHelper {

        const val ID = "nuclear_rod_irradiator"
        const val NAME = "Nuclear Rod Irradiator"

        private val CASING = SimpleMember.forBlock { MIMaterials.NUCLEAR_ALLOY.getPart(MIParts.MACHINE_CASING_SPECIAL).asBlock() }
        private val CASING_PIPE = SimpleMember.forBlock { MIMaterials.NUCLEAR_ALLOY.getPart(MIParts.MACHINE_CASING_PIPE).asBlock() }

        private val NUCLEAR_HATCH = HatchFlags.Builder()
            .with(HatchTypes.NUCLEAR_ITEM)
            .build()

        private val ITEM_HATCH = HatchFlags.Builder()
            .with(HatchTypes.ITEM_INPUT)
            .build()
        private val ENERGY_HATCH = HatchFlags.Builder()
            .with(HatchTypes.ENERGY_INPUT)
            .build()

        private val BOTTOM_LAYER = listOf(
            "_###_",
            "#####",
            "#####",
            "#####",
            "_###_"
        )

        private val MIDDLE_LAYER = listOf(
            "#####",
            "#@@@#",
            "#@@@#",
            "#@@@#",
            "#####"
        )

        private val TOP_LAYER = listOf(
            "_###_",
            "#xxx#",
            "#xox#",
            "#xxx#",
            "_###_"
        )

        override val pattern = listOf(
            BOTTOM_LAYER,
            MIDDLE_LAYER,
            MIDDLE_LAYER,
            MIDDLE_LAYER,
            TOP_LAYER
        )

        override val materialRules: Map<(Char, Int) -> Boolean, SimpleMember>
            get() = mapOf(
                { char: Char, _: Int -> char in arrayOf('#', 'x', 'o') } to CASING,
                { char: Char, _: Int -> char == '@' } to CASING_PIPE
            )

        override val hatchPredicate: Map<(Char, Int) -> Boolean, HatchFlags>
            get() = mapOf(
                { char: Char, _: Int -> char == '#' } to ENERGY_HATCH,
                { char: Char, _: Int -> char == 'x' } to NUCLEAR_HATCH,
                { char: Char, _: Int -> char == 'o' } to ITEM_HATCH
            )

        override val controllerXOffset = -2

        val SHAPE = ShapeTemplate.Builder(MachineCasings.NUCLEAR)
            .addLayer(-2, 0)
            .addLayer(-1, 1)
            .addLayer(0, 2)
            .addLayer(1, 3)
            .addLayer(2, 4)
            .build()

        private val DEFAULT_NEUTRON_SOURCE = IrradiatorNeutronSource(
            2048,
            4096L,
            null,
            IrradiatorNeutronSource.Type.NONE,
            .0f,
            1
        )

    }

    private val activeShape = ActiveShapeComponent(arrayOf(SHAPE))
    private val inventory = MultiblockInventoryComponent()
    private val isActive = IsActiveComponent()
    private val redstoneControl = RedstoneControlComponent()

    private val energyInputs = mutableListOf<EnergyComponent>()
    private val nuclearHatches = mutableListOf<NuclearHatch>()

    private var neutronSource: IrradiatorNeutronSource? = null
    private var forceSourceRefresh = false

    init {
        registerComponents(activeShape, isActive, redstoneControl)

        registerGuiComponent(
            SlotPanel(this)
                .withRedstoneControl(redstoneControl)
        )

        registerGuiComponent(
            ModularMultiblockGui(0, 40) { content ->
                content!!.add(statusComponent(), if (isShapeValid) WHITE else RED)
                neutronSource?.let {
                    val irradiation = TextHelper.getAmount(it.irradiation.toDouble())
                    content.add(
                        YAI.TEXT.irradiatorNeutronSourceIrradiation(irradiation.digit, irradiation.unit),
                    )
                    content.add(YAI.TEXT.irradiatorNeutronSourceType(it.type.component))
                }

            }
        )
    }

    override fun tick() {
        if (level!!.isClientSide) return
        link()

        if (forceSourceRefresh || level!!.gameTime % (3*20) == 0L) {
            neutronSource = getNeutronSource()
            forceSourceRefresh = false
        }

        if (neutronSource == null) {
            isActive.updateActive(false, this)
            return
        }

        var newActive = false
        if (consumeEu(Simulation.SIMULATE)) {
            newActive = absorb()
            consumeEu(Simulation.ACT)
        }
        isActive.updateActive(newActive, this)
    }

    override fun onRematch(shapeMatcher: ShapeMatcher) {
        if (!shapeMatcher.isMatchSuccessful) return
        inventory.rebuild(shapeMatcher)
        energyInputs.clear()
        nuclearHatches.clear()
        for (hatch in shapeMatcher.matchedHatches) {
            hatch.appendEnergyInputs(energyInputs)
            if (hatch is NuclearHatch) {
                nuclearHatches.add(hatch)
            }
        }
    }

    override fun useItemOn(player: Player, hand: InteractionHand, face: Direction): ItemInteractionResult {
        var result = super.useItemOn(player, hand, face)
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand)
        }
        return result
    }

    override fun getMultiblockInventoryComponent() = inventory

    override fun getInventory() = MIInventory.EMPTY

    override fun getEnergyComponents() = energyInputs

    override fun getActiveShape() = SHAPE

    override fun getMachineModelData(): MachineModelClientData {
        return MachineModelClientData(null, orientation.facingDirection).active(isActive.isActive)
    }

    private fun consumeEu(simulation: Simulation): Boolean {
        val toConsume = neutronSource?.eu ?: return false
        var total = 0L

        for (energyComponent in energyInputs) {
            total += energyComponent.consumeEu(toConsume - total, simulation)
        }

        return total == toConsume
    }

    @Suppress("DEPRECATION")
    private fun absorb(): Boolean {
        if (neutronSource == null || nuclearHatches.isEmpty()) return false

        val restriction = neutronSource!!.restrictedTo.getOrNull()

        var remainActive = false
        for (hatch in nuclearHatches) {
            val itemVariant = hatch.variant as ItemVariant

            if (itemVariant.item !is NuclearFuel) continue
            val abs = itemVariant.item as NuclearAbsorbable

            // FIXME Kotlin 2.2: Rewrite with ?.let block
            if (restriction != null) {
                val check = restriction.map(
                    { resourceKey -> abs.builtInRegistryHolder().key() == resourceKey },
                    { tagKey -> abs.builtInRegistryHolder().`is`(tagKey) }
                )

                if (!check) continue
            }
            remainActive = true

            val stack = itemVariant.toStack(hatch.variantAmount.toInt())
            val newDesintegration = (abs.getRemainingDesintegrations(stack) - neutronSource!!.irradiation).coerceAtLeast(0)
            abs.setRemainingDesintegrations(stack, newDesintegration)

            if (abs.getRemainingDesintegrations(stack) == 0) {
                Transaction.openRoot().use { tx ->
                    val absStack = hatch.inventory.itemStacks[0]
                    absStack.updateSnapshots(tx)
                    absStack.setAmount(0)
                    absStack.setKey(ItemVariant.blank())
                    if (abs.neutronProduct != null) {
                        val inserted = hatch.inventory.itemStorage.insert(
                            abs.neutronProduct,
                            abs.neutronProductAmount, tx,
                            { it.canPipesExtract() },
                            true
                        )

                        if (inserted == abs.neutronProductAmount) {
                            tx.commit()
                        }
                    } else {
                        tx.commit()
                    }
                }
            } else {
                hatch.inventory.itemStacks[0].setKey(ItemVariant.of(stack))
            }
        }

        if ((level!!.gameTime % neutronSource!!.probabilityCheckCooldown == 0L) &&
            neutronSource!!.type.usesItem &&
            ThreadLocalRandom.current().nextFloat() < neutronSource!!.probability) {
            var keepNeutronSource = false
            for (input in inventory.itemInputs) {
                if (input.isEmpty || input.amount == 0L) continue

                when (neutronSource!!.type) {
                    IrradiatorNeutronSource.Type.CONSUMPTION -> {
                        input.amount--
                    }
                    IrradiatorNeutronSource.Type.DURABILITY -> {
                        var removeItem = false
                        val updated = input.toStack().apply {
                            this.hurtAndBreak(1, level as ServerLevel, null, { _ ->
                                if (this.damageValue >= this.maxDamage) {
                                    removeItem = true
                                }
                            })
                        }
                        input.setKey(if (removeItem) ItemVariant.blank() else ItemVariant.of(updated))
                    }
                    else -> {}
                }
                keepNeutronSource = !input.isResourceBlank && input.amount != 0L
                break
            }

            if (!keepNeutronSource) {
                neutronSource = null
                forceSourceRefresh = true
            }
        }

        return remainActive
    }

    private fun getNeutronSource(): IrradiatorNeutronSource? {
        if (IrradiatorNeutronSource.all().isEmpty()) return DEFAULT_NEUTRON_SOURCE

        for (input in inventory.itemInputs) {
            if (input.isEmpty || input.amount == 0L) continue

            return IrradiatorNeutronSource.getFor(input.variant.item)
        }
        return null
    }

    private fun statusComponent(): Component {
        if (!isShapeValid) return MIText.MultiblockShapeInvalid.text()

        return (if (isActive.isActive) MIText.MultiblockStatusActive else MIText.MultiblockShapeValid).text()
    }

}