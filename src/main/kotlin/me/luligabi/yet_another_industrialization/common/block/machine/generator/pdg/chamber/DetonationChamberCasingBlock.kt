package me.luligabi.yet_another_industrialization.common.block.machine.generator.pdg.chamber

import aztech.modern_industrialization.machines.MachineBlock
import aztech.modern_industrialization.machines.MachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import java.util.function.BiFunction

class DetonationChamberCasingBlock(
    blockEntityConstructor: BiFunction<BlockPos, BlockState, out MachineBlockEntity>, properties: Properties
) : MachineBlock(blockEntityConstructor, properties.strength(50f, 1200f)) {

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hit: BlockHitResult) = InteractionResult.PASS

    override fun useItemOn(handStack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult) = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
}