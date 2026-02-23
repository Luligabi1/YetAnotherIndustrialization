package me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.chamber

import aztech.modern_industrialization.inventory.MIInventory
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.MachineComponent
import aztech.modern_industrialization.machines.components.OrientationComponent
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.models.MachineModelClientData
import aztech.modern_industrialization.util.Tickable
import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.block.machine.generator.multiblock.pdg.PulseDetonationGeneratorBlockEntity
import me.luligabi.yet_another_industrialization.mixin.AbstractCraftingMultiblockBlockEntityAccessor
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

class DetonationChamberCasingBlockEntity(bep: BEP): MachineBlockEntity(
    bep,
    MachineGuiParameters.Builder(ID, false).build(),
    OrientationComponent.Params.noFacingNoOutput()
), Tickable {

    companion object {
        const val ID = "detonation_chamber_casing"
        const val NAME = "Detonation Chamber Casing"
    }

    val data = Data()

    init {
        registerComponents(data)
    }

    var controller: PulseDetonationGeneratorBlockEntity? = null

    override fun tick() {
        if (level!!.isClientSide()) return

        if (controller == null || !(controller as AbstractCraftingMultiblockBlockEntityAccessor).isActive.isActive) {
            if (data.progress <= 0f) return
            data.progress -= 0.05f
            data.onSequence = false
            sync()
            return
        }
        if (!data.onSequence) {
            data.progress = controller!!.crafterComponent.progress
            if (data.progress > 0.99f) data.onSequence = true
        } else {
            val progress = controller!!.crafterComponent.progress
            val progressSqr = progress * progress

            // visual progress on a repeated recipe starts at 68% (around when the red becomes more visible)
            data.progress = (0.68f + (progressSqr * 0.68f)).coerceAtMost(1f)
        }
        sync()
    }

    override fun openMenu(player: ServerPlayer) { // FIXME remove onUse to prevent attempt to open menu
    }

    override fun getInventory() = MIInventory.EMPTY

    override fun getMachineModelData() = MachineModelClientData(YAIMachines.Casings.DETONATION_CHAMBER_CASING)

    class Data: MachineComponent {

        var progress = 0f
        var onSequence = false

        override fun writeNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
            writeClientNbt(tag, registries)
            tag.putBoolean("onSequence", onSequence)
        }

        override fun readNbt(tag: CompoundTag, registries: HolderLookup.Provider, isUpgradingMachine: Boolean) {
            readClientNbt(tag, registries)
            onSequence = tag.getBoolean("onSequence")
        }

        override fun writeClientNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
            tag.putFloat("progress", progress)
        }

        override fun readClientNbt(tag: CompoundTag, registries: HolderLookup.Provider) {
            progress = tag.getFloat("progress")
        }

    }

}