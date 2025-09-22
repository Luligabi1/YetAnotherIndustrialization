package me.luligabi.yet_another_industrialization.common.item

import aztech.modern_industrialization.MIComponents
import aztech.modern_industrialization.machines.MachineBlockEntity
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity
import dev.technici4n.grandpower.api.ISimpleEnergyItem
import me.luligabi.yet_another_industrialization.common.YAI
import me.luligabi.yet_another_industrialization.common.misc.YAISounds
import me.luligabi.yet_another_industrialization.common.misc.YAITags
import me.luligabi.yet_another_industrialization.common.util.MACHINE_REMOVER_STYLE
import me.luligabi.yet_another_industrialization.common.util.YAIText
import me.luligabi.yet_another_industrialization.common.util.applyStyle
import me.luligabi.yet_another_industrialization.mixin.MultiblockMachineBlockEntityAccessor
import me.luligabi.yet_another_industrialization.mixin.UseOnContextAccessor
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.HitResult
import kotlin.math.roundToInt

class MachineRemoverItem(properties: Properties) : Item(
    properties.stacksTo(1).component(MIComponents.ENERGY, 0L)
), ISimpleEnergyItem {

    companion object {
        val ENERGY_CAPACITY
            get() = YAI.CONFIG.machineRemover().capacity()

        val SINGLE_BLOCK_REMOVE_COST
            get() = YAI.CONFIG.machineRemover().singleBlockRemoveCost()

        val MULTIBLOCK_REMOVE_BASE_COST
            get() = YAI.CONFIG.machineRemover().multiblockBaseRemoveCost()

        val MULTIBLOCK_REMOVE_BLOCK_COST
            get() = YAI.CONFIG.machineRemover().multiblockBlockRemoveCost()
    }

    override fun useOn(ctx: UseOnContext): InteractionResult {
        if (ctx.level.isClientSide) return InteractionResult.FAIL
        if ((ctx as UseOnContextAccessor).hitResult.type != HitResult.Type.BLOCK) return InteractionResult.PASS

        val machine = ctx.level.getBlockEntity(ctx.clickedPos) as? MachineBlockEntity ?: return InteractionResult.PASS
        if (machine.type.builtInRegistryHolder()!!.`is`(YAITags.MACHINE_REMOVER_BANNED)) {
            ctx.player!!.sendError(YAIText.MACHINE_REMOVER_BANNED)
            return InteractionResult.sidedSuccess(ctx.level.isClientSide)
        }

        val success = when (machine) {
            is MultiblockMachineBlockEntity -> {
                removeMultiblock(ctx.itemInHand, ctx.player!!, ctx.level, ctx.clickedPos, machine)
            }
            else -> {
                removeSingleMachine(ctx.itemInHand, ctx.player!!, ctx.level, ctx.clickedPos, machine)
            }
        }
        if (success) {
            ctx.level.playSound(
                null, ctx.clickedPos,
                YAISounds.MACHINE_REMOVER_REMOVE.get(),
                SoundSource.PLAYERS, 1f, 1f
            )
            YAI.CONFIG.machineRemover().cooldownTicks().let {
                if (it > 0) ctx.player!!.cooldowns.addCooldown(this, it)
            }
        }
        return if (success) InteractionResult.sidedSuccess(ctx.level.isClientSide) else InteractionResult.FAIL
    }

    private fun removeMultiblock(stack: ItemStack, player: Player, level: Level, pos: BlockPos, controller: MultiblockMachineBlockEntity): Boolean {
        if (!controller.isShapeValid) {
            return removeSingleMachine(stack, player, level, pos, controller)
        }

        val members = (controller as MultiblockMachineBlockEntityAccessor).shapeMatcher.positions
        if (members.size > 128) {
            player.sendError(YAIText.MACHINE_REMOVER_TOO_LARGE)
            return false
        }

        if (tryUseEnergy(stack, getMultiblockCost(members.size))) {
            members.forEach { removeBlock(level, player, it, pos) }
            removeBlock(level, player, pos, pos, controller)
            return true
        } else {
            player.sendError(YAIText.MACHINE_REMOVER_INSUFFICIENT_ENERGY)
            return false
        }
    }

    private fun removeSingleMachine(stack: ItemStack, player: Player, level: Level, pos: BlockPos, machine: MachineBlockEntity): Boolean {
        if (!tryUseEnergy(stack, SINGLE_BLOCK_REMOVE_COST)) {
            player.sendError(YAIText.MACHINE_REMOVER_INSUFFICIENT_ENERGY)
            return false
        }

        removeBlock(level, player, pos, pos, machine)
        return true
    }

    private fun removeBlock(
        level: Level,
        player: Player,
        blockPos: BlockPos, dropPos: BlockPos,
        machine: MachineBlockEntity? = null
    ) {
        level.getBlockState(blockPos).let {
            it.block.playerDestroy(
                level, player, dropPos, it,
                machine ?: level.getBlockEntity(blockPos),
                ChaChaRealSmoothItem.create(level)
            )
        }
        level.setBlock(
            blockPos, Blocks.AIR.defaultBlockState(),
            Block.UPDATE_ALL or Block.UPDATE_SUPPRESS_DROPS
        )
    }

    private fun getMultiblockCost(blockCount: Int): Long {
        return MULTIBLOCK_REMOVE_BASE_COST + (blockCount * MULTIBLOCK_REMOVE_BLOCK_COST)
    }

    override fun isBarVisible(stack: ItemStack) = !stack.has(DataComponents.CUSTOM_DATA)

    override fun getBarWidth(stack: ItemStack) = (getStoredEnergy(stack) / ENERGY_CAPACITY.toDouble() * 13).roundToInt()

    override fun getBarColor(stack: ItemStack) = 0xFF0000

    override fun getEnergyComponent() = MIComponents.ENERGY.get()

    override fun getEnergyCapacity(stack: ItemStack) = ENERGY_CAPACITY

    override fun getEnergyMaxInput(stack: ItemStack) = ENERGY_CAPACITY

    override fun getEnergyMaxOutput(stack: ItemStack) = 0L


    private fun Player.sendError(message: YAIText) {
        displayClientMessage(message.text().applyStyle(MACHINE_REMOVER_STYLE), true)
    }

}