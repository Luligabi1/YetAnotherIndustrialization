package me.luligabi.yet_another_industrialization.common.block.machine.util.flexible_recipe

import aztech.modern_industrialization.api.machine.holder.EnergyListComponentHolder
import aztech.modern_industrialization.machines.BEP
import aztech.modern_industrialization.machines.components.*
import aztech.modern_industrialization.machines.gui.MachineGuiParameters
import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate
import aztech.modern_industrialization.util.Simulation
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player

abstract class ElectricFlexibleRecipeMultiblockBlockEntity(
    bep: BEP,
    guiParams: MachineGuiParameters,
    orientationParams: OrientationComponent.Params,
    shapeTemplates: Array<ShapeTemplate>
) : FlexibleRecipeMultiblockBlockEntity(
    bep,
    guiParams,
    orientationParams,
    shapeTemplates
), EnergyListComponentHolder {

    protected val redstoneControl = RedstoneControlComponent()
    protected val energyInputs = mutableListOf<EnergyComponent>()

    init {
        registerComponents(redstoneControl)
    }

    override fun onRematch(shapeMatcher: ShapeMatcher) {
        super.onRematch(shapeMatcher)
        if (shapeMatcher.isMatchSuccessful) {
            energyInputs.clear()
            for (hatch in shapeMatcher.matchedHatches) {
                hatch.appendEnergyInputs(energyInputs)
            }
        }
    }

    override fun consumeEu(max: Long, simulation: Simulation): Long {
        var total: Long = 0

        for (energyComponent in energyInputs) {
            total += energyComponent.consumeEu(max - total, simulation)
        }

        return total
    }

    override fun isEnabled() = redstoneControl.doAllowNormalOperation(this)

    override fun useItemOn(player: Player, hand: InteractionHand, face: Direction): ItemInteractionResult {
        var result = super.useItemOn(player, hand, face)
//        if (!result.consumesAction()) { TODO add flexible recipe overclocking
//            result = LubricantHelper.onUse(this.crafter, player, hand)
//        }
        if (!result.consumesAction()) {
            result = components.mapOrDefault(
                UpgradeComponent::class.java,
                { it.onUse(this, player, hand) },
                result
            )
        }
        if (!result.consumesAction()) {
            result = redstoneControl.onUse(this, player, hand)
        }
        if (!result.consumesAction()) {
            result = components.mapOrDefault(
                OverdriveComponent::class.java,
                { it.onUse(this, player, hand) },
                result
            )
        }
        return result
    }

    override fun getEnergyComponents() = energyInputs

}
