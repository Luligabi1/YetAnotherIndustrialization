package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.MICapabilities
import aztech.modern_industrialization.api.energy.EnergyApi
import aztech.modern_industrialization.api.energy.MIEnergyStorage
import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.multiblocks.HatchBlockEntity
import aztech.modern_industrialization.machines.multiblocks.HatchType
import me.luligabi.yet_another_industrialization.common.misc.YAIHatchTypes
import me.luligabi.yet_another_industrialization.common.util.EmptyEnergyAccess
import me.luligabi.yet_another_industrialization.common.util.YAIText
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntityType

class LargeStorageUnitHatch(bep: BEP, private val input: Boolean, blockId: ResourceLocation) : HatchBlockEntity(
    bep,
    MachineGuiParameters.Builder(blockId, false).build(),
    OrientationComponent.Params(false, false, false)
) {

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

    override fun getInventory() = MIInventory.EMPTY

    override fun openMenu(player: ServerPlayer) {
        if (controller != null) {
            controller!!.openMenu(player)
        } else {
            player.displayClientMessage(YAIText.NO_LARGE_STORAGE_UNIT.text().withStyle(ChatFormatting.RED), true)
        }
    }

    override fun getHatchType(): HatchType {
        return if (input) YAIHatchTypes.LARGE_STORAGE_UNIT_INPUT else YAIHatchTypes.LARGE_STORAGE_UNIT_OUTPUT
    }

    override fun upgradesToSteel() = false

    override fun unlink() {
        super.unlink()
        controller = null
        invalidateCapabilities()
    }

    fun setController(controller: LargeStorageUnitBlockEntity) {
        this.controller = controller
        invalidateCapabilities()
    }

    private fun getStorage(): MIEnergyStorage {
        if (controller == null) return EmptyEnergyAccess
        return if (input) controller!!.insertable else controller!!.extractable
    }

}