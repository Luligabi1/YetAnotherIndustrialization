package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.MICapabilities
import aztech.modern_industrialization.api.energy.EnergyApi
import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity
import aztech.modern_industrialization.machines.multiblocks.HatchType
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.transaction.Transaction
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes
import me.luligabi.yet_another_industrialization.common.util.EmptyEnergyAccess
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntityType

class LargeStorageUnitHatch(bep: BEP, private val input: Boolean, blockId: ResourceLocation) : HatchBlockEntity(
    bep,
    MachineGuiParameters.Builder(blockId, false).build(),
    OrientationComponent.Params(false, false, false)
), EnergyListComponentHolder {

    companion object {

        const val ID_INPUT = "large_storage_unit_input_hatch"
        const val ID_OUTPUT = "large_storage_unit_output_hatch"

        fun registerEnergyApi(bet: BlockEntityType<*>) {
            MICapabilities.onEvent {
                it.registerBlockEntity(
                    EnergyApi.SIDED, bet,
                    { be, _ -> (be as LargeStorageUnitHatch).getStorage() }
                )
            }
        }
    }

    private var controller: LargeStorageUnitBlockEntity? = null

    override fun useItemOn(player: Player, hand: InteractionHand, face: Direction): ItemInteractionResult {
        val energyItem = player.getItemInHand(hand).getCapability(EnergyApi.ITEM)
        val stackSize = player.getItemInHand(hand).count
        if (energyItem != null) {
            if (!player.level().isClientSide()) {

                val energy = getStorage()
                if (energy == EmptyEnergyAccess) return super.useItemOn(player, hand, face)

                if (input) {
                    repeat(10000) {
                        Transaction.openRoot().use { transaction ->
                            val extracted = energyItem.extract((energy.capacity - energy.amount) / stackSize, false)
                            if (extracted == 0L) {
                                return@use
                            }

                            energy.receive(extracted * stackSize, false)
                            transaction.commit()
                        }
                    }
                } else {
                    repeat(10000) {
                        Transaction.openRoot().use { transaction ->
                            val inserted = energyItem.receive(energy.amount / stackSize, false)
                            if (inserted == 0L) {
                                return@use
                            }

                            energy.extract(inserted * stackSize, false)
                            transaction.commit()
                        }
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide())
        }
        return super.useItemOn(player, hand, face)
    }


    override fun getEnergyComponents() = controller?.energy?.let {
        listOf(it)
    } ?: emptyList()

    override fun openMenu(player: ServerPlayer) {
        if (controller != null) {
            controller!!.openMenu(player)
        } else {
            player.displayClientMessage(YAI.TEXT.noLargeStorageUnit(), true)
        }
    }

    override fun getHatchType(): HatchType {
        return if (input) YAIHatchTypes.LARGE_STORAGE_UNIT_INPUT else YAIHatchTypes.LARGE_STORAGE_UNIT_OUTPUT
    }

    override fun unlink() {
        super.unlink()
        controller = null
        invalidateCapabilities()
    }


    override fun getComparatorOutput(): Int {
        val energy = getStorage()
        if (energy == EmptyEnergyAccess) return 0
        val fillPercentage = energy.amount.toDouble() / energy.capacity
        return Mth.floor(fillPercentage * 14) + (if (energy.amount > 0) 1 else 0)
    }

    override fun hasComparatorOutput() = true

    override fun upgradesToSteel() = false

    override fun getInventory() = MIInventory.EMPTY

    fun setController(controller: LargeStorageUnitBlockEntity) {
        this.controller = controller
        invalidateCapabilities()
    }

    private fun getStorage(): MIEnergyStorage {
        if (controller == null) return EmptyEnergyAccess
        return if (input) controller!!.insertable else controller!!.extractable
    }

}