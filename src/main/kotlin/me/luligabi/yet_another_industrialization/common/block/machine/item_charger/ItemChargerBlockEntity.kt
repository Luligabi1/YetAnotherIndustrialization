package me.luligabi.yet_another_industrialization.common.block.machine.item_charger

import aztech.modern_industrialization.MICapabilities
import aztech.modern_industrialization.api.energy.CableTierHolder
import aztech.modern_industrialization.api.energy.EnergyApi
import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.api.machine.holder.EnergyComponentHolder
import aztech.modern_industrialization.inventory.ConfigurableItemStack
import aztech.modern_industrialization.inventory.SlotPositions
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.components.*
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.guicomponents.AutoExtract
import aztech.modern_industrialization.machines.guicomponents.EnergyBar
import aztech.modern_industrialization.machines.guicomponents.ProgressBar
import aztech.modern_industrialization.machines.guicomponents.SlotPanel
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction
import aztech.modern_industrialization.util.Tickable
import dev.technici4n.grandpower.api.ILongEnergyStorage
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.util.setContent
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

class ItemChargerBlockEntity(
    bep: BEP
): MachineBlockEntity(
    bep,
    MachineGuiParameters.Builder(YAI.id(ID), true).build(),
    OrientationComponent.Params(true, true, false)
), EnergyComponentHolder, CableTierHolder, Tickable {

    companion object {

        const val ID = "item_charger"
        const val NAME = "Item Charger"

        fun registerEnergyApi(bet: BlockEntityType<*>) {
            MICapabilities.onEvent { event: RegisterCapabilitiesEvent? ->
                event!!.registerBlockEntity(
                    EnergyApi.SIDED,
                    bet,
                    { be, _ -> (be as ItemChargerBlockEntity).insertable })
            }
        }
    }

    private val redstoneControl = RedstoneControlComponent()
    private val casing = CasingComponent()
    private val isActive = IsActiveComponent()
    private val energy = EnergyComponent(this, casing::getEuCapacity)
    private val insertable = energy.buildInsertable(casing::canInsertEu)
    private val extractable = energyComponent.buildExtractable({ true })
    private val inventory = run {
        val itemInputs = listOf(ConfigurableItemStack.standardInputSlot())
        val itemOutputs = listOf(ConfigurableItemStack.standardOutputSlot())

        val itemPositions = SlotPositions.Builder()
            .addSlot(56, 35) // input
            .addSlot(102, 35) // output
            .build()

        MachineInventoryComponent(itemInputs, itemOutputs, emptyList(), emptyList(), itemPositions, SlotPositions.empty())
    }

    init {
        registerComponents(redstoneControl, casing, isActive, energy, inventory)

        registerGuiComponent(EnergyBar(EnergyBar.Params(18, 30), energy::getEu, energy::getCapacity))
        registerGuiComponent(
            SlotPanel(this)
                .withRedstoneControl(redstoneControl)
                .withCasing(casing)
        )
        registerGuiComponent(AutoExtract(orientation))
        registerGuiComponent(ProgressBar(ProgressBar.Params(77, 33, "yai_charge", true), ::getProgress))
    }
    
    override fun tick() {
        if (level!!.isClientSide) return

        if (orientation.extractItems) {
            inventory.inventory.autoExtractItems(level!!, worldPosition, orientation.outputDirection)
        }

        if (!redstoneControl.doAllowNormalOperation(this)) {
            isActive.updateActive(false, this)
            return
        }

        val cStack = inventory.inventory.itemStacks.getOrNull(0)
        val stack = cStack?.toStack()
        val inputStorage = stack?.getCapability(MIEnergyStorage.ITEM)
        if (inputStorage != null && (inputStorage.amount == inputStorage.capacity)) {
            Transaction.openRoot().use { tx ->
                val input = inventory.itemInputs[0]
                val inserted = inventory.inventory.itemStorage.insert(
                    input.variant, input.amount, tx,
                    ConfigurableItemStack::canPipesExtract, false
                )
                input.apply {
                    updateSnapshots(tx)
                    setAmount(0)
                    setKey(ItemVariant.blank())
                }

                if (inserted > 0) tx.commit()
            }
        }

        val newActive = attemptCharge(cStack, stack, inputStorage)
        isActive.updateActive(newActive, this)
        setChanged()
    }

    private fun attemptCharge(cStack: ConfigurableItemStack?, stack: ItemStack?, inputStorage: ILongEnergyStorage?): Boolean {
        if (cStack == null || stack == null || inputStorage == null) return false
        if (cStack.isEmpty) return false

        val stack = cStack.toStack()
        val capability = stack.getCapability(MIEnergyStorage.ITEM) ?: return false
        if (extractable.extract(cableTier.maxTransfer, true) > 0 && capability.receive(cableTier.maxTransfer, true) > 0) {
            extractable.extract(cableTier.maxTransfer, false)
            capability.receive(cableTier.maxTransfer, false)
            cStack.setContent(stack)
            return true
        }

        return false
    }

    override fun useItemOn(player: Player, hand: InteractionHand, face: Direction): ItemInteractionResult {
        var result = super.useItemOn(player, hand, face)
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand)
        }
        if (!result.consumesAction()) {
            result = casing.onUse(this, player, hand)
        }
        return result
    }

    override fun getMachineModelData(): MachineModelClientData {
        val data = MachineModelClientData(casing.casing).apply {
            orientation.writeModelData(this)
            isActive = this@ItemChargerBlockEntity.isActive.isActive
        }
        return data
    }

    override fun getInventory() = inventory.inventory

    override fun getEnergyComponent() = energy

    override fun getCableTier() = casing.cableTier

    private fun getProgress(): Float {
        val itemStorage = getInputEnergyStorage() ?: return .0f
        return itemStorage.amount.toFloat() / itemStorage.capacity
    }

    private fun getInputEnergyStorage(): ILongEnergyStorage? {
        val item = inventory.inventory.itemStacks.getOrNull(0) ?: return null
        if (item.isEmpty) return null
        return item.toStack().getCapability(MIEnergyStorage.ITEM)
    }

}