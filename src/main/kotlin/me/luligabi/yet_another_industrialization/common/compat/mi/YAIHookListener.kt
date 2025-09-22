package me.luligabi.yet_another_industrialization.common.compat.mi

import me.luligabi.yet_another_industrialization.common.block.machine.YAIMachines
import me.luligabi.yet_another_industrialization.common.compat.recipeviewer.LargeStorageUnitTierCategory
import me.luligabi.yet_another_industrialization.common.misc.YAITooltips
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookEntrypoint
import net.swedz.tesseract.neoforge.compat.mi.hook.MIHookListener
import net.swedz.tesseract.neoforge.compat.mi.hook.context.listener.*

@MIHookEntrypoint
class YAIHookListener: MIHookListener {

    override fun singleBlockCraftingMachines(hook: SingleBlockCraftingMachinesMIHookContext) {
        YAIMachines.singleBlockCrafting(hook)
    }

    override fun singleBlockSpecialMachines(hook: SingleBlockSpecialMachinesMIHookContext) {
        YAIMachines.singleBlockSpecial(hook)
    }

    override fun multiblockMachines(hook: MultiblockMachinesMIHookContext) {
        YAIMachines.multiblockMachines(hook)
    }

    override fun machineRecipeTypes(hook: MachineRecipeTypesMIHookContext) {
        YAIMachines.recipeTypes(hook)
    }

    override fun machineCasings(hook: MachineCasingsMIHookContext) {
        YAIMachines.machineCasings(hook)
    }

    override fun hatches(hook: HatchMIHookContext) {
        YAIMachines.hatches(hook)
    }

    override fun viewerSetup(hook: ViewerSetupMIHookContext) {
        hook.register(LargeStorageUnitTierCategory())
    }

    override fun tooltips() {
        YAITooltips
    }

}