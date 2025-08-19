package me.luligabi.hostile_neural_industrialization.common.compat.mi

import me.luligabi.hostile_neural_industrialization.common.block.machine.HNIMachines
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookEntrypoint
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookListener
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.*

@MIHookEntrypoint
class HNIHookListener: MIHookListener {

    override fun singleBlockCraftingMachines(hook: SingleBlockCraftingMachinesMIHookContext) {
        HNIMachines.singleBlockCrafting(hook)
    }

    override fun singleBlockSpecialMachines(hook: SingleBlockSpecialMachinesMIHookContext) {
        HNIMachines.singleBlockSpecial(hook)
    }

    override fun multiblockMachines(hook: MultiblockMachinesMIHookContext) {
        HNIMachines.multiblockMachines(hook)
    }

    override fun machineRecipeTypes(hook: MachineRecipeTypesMIHookContext) {
        HNIMachines.recipeTypes(hook)
    }

    override fun machineCasings(hook: MachineCasingsMIHookContext) {
        HNIMachines.machineCasings(hook)
    }

}