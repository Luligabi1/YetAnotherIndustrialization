package me.luligabi.yet_another_industrialization.common.item

import aztech.modern_industrialization.MIComponents
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import dev.technici4n.grandpower.api.ISimpleEnergyItem
import me.luligabi.yet_another_industrialization.common.util.MACHINE_REMOVER_STYLE
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import me.luligabi.yet_another_industrialization.mixin.MultiblockMachineBlockEntityAccessor
import me.luligabi.yet_another_industrialization.mixin.UseOnContextAccessor
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.HitResult
import kotlin.math.roundToInt

class MachineRemoverItem(properties: Properties) : Item(
    properties.stacksTo(1).component(MIComponents.ENERGY, 0L)
), ISimpleEnergyItem {

    companion object {
        const val ENERGY_CAPACITY = (1 shl 16).toLong()

        const val REMOVE_COST = 40L
        const val MULTIBLOCK_REMOVE_BASE_COST = 120L
    }

    override fun useOn(ctx: UseOnContext): InteractionResult {
        if (ctx.level.isClientSide) return InteractionResult.FAIL
        if ((ctx as UseOnContextAccessor).hitResult.type != HitResult.Type.BLOCK) return InteractionResult.PASS

        val machine = ctx.level.getBlockEntity(ctx.clickedPos) as? MachineBlockEntity ?: return InteractionResult.PASS
        when (machine) {
            is MultiblockMachineBlockEntity -> {
                removeMultiblock(ctx.player!!, ctx.level as ServerLevel, ctx.clickedPos, machine)
            }
            else -> {
                removeBlock(ctx.level as ServerLevel, ctx.clickedPos)
            }
        }
        return InteractionResult.SUCCESS
    }


    private fun removeMultiblock(player: Player, level: ServerLevel, pos: BlockPos, controller: MultiblockMachineBlockEntity) {
        if (!controller.isShapeValid) {
            removeBlock(level, pos)
            return
        }

        val members = (controller as MultiblockMachineBlockEntityAccessor).shapeMatcher.positions
        if (members.size > 64) {
            player.sendSystemMessage(YAIText.MACHINE_REMOVER_TOO_LARGE.text().applyStyle(MACHINE_REMOVER_STYLE))
            return
        }
        members.forEach { removeBlock(level, it) }
        removeBlock(level, pos)
    }

    private fun removeBlock(level: ServerLevel, pos: BlockPos) {
        //Block.dropResources(level.getBlockState(pos), level, pos, level.getBlockEntity(pos))
        level.removeBlock(pos, false)
    }


    private fun getMultiblockCost(blockCount: Int): Long {
        return MULTIBLOCK_REMOVE_BASE_COST + (blockCount * (REMOVE_COST / 10))
    }





    override fun isBarVisible(stack: ItemStack) = true

    override fun getBarWidth(stack: ItemStack) = (getStoredEnergy(stack) / ENERGY_CAPACITY.toDouble() * 13).roundToInt()

    override fun getEnergyComponent() = MIComponents.ENERGY.get()

    override fun getEnergyCapacity(stack: ItemStack) = ENERGY_CAPACITY

    override fun getEnergyMaxInput(stack: ItemStack) = ENERGY_CAPACITY

    override fun getEnergyMaxOutput(stack: ItemStack) = 0L

}