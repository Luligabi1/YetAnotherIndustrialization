package me.luligabi.yet_another_industrialization.client.model.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.lighting.LevelLightEngine
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockFakeLevel(var model: MultiBlockModel, var realLevel: BlockAndTintGetter, var lightAndTintPosition: BlockPos) : BlockAndTintGetter {

    override fun getShade(pDirection: Direction, pShade: Boolean): Float {
        return this.realLevel.getShade(pDirection, pShade)
    }

    override fun getLightEngine(): LevelLightEngine {
        return this.realLevel.getLightEngine()
    }


    override fun getBrightness(pLightType: LightLayer, pBlockPos: BlockPos): Int {
        return if (this.lightAndTintPosition == BlockPos.ZERO) 255 else this.realLevel.getBrightness(
            pLightType,
            this.lightAndTintPosition
        )
    }

    override fun getBlockTint(pBlockPos: BlockPos, pColorResolver: ColorResolver): Int {
        return this.realLevel.getBlockTint(this.lightAndTintPosition, pColorResolver)
    }

    override fun getBlockEntity(pPos: BlockPos): BlockEntity? {
        return null
    }

    override fun getBlockState(pPos: BlockPos): BlockState {
        val voxel = model.blocks[pPos]
        if (voxel == null) {
            return Blocks.AIR.defaultBlockState()
        }
        return voxel.state
    }

    override fun getFluidState(pPos: BlockPos): FluidState {
        return Fluids.EMPTY.defaultFluidState()
    }

    override fun getHeight(): Int {
        return this.model.geometry.getSize().getY()
    }

    override fun getMinBuildHeight(): Int {
        return 0
    }

    override fun getRawBrightness(blockPos: BlockPos, amount: Int): Int {
        return realLevel.getRawBrightness(this.lightAndTintPosition, amount)
    }

    override fun getShade(normalX: Float, normalY: Float, normalZ: Float, shade: Boolean): Float {
        return realLevel.getShade(normalX, normalY, normalZ, shade)
    }

    override fun canSeeSky(blockPos: BlockPos): Boolean {
        return realLevel.canSeeSky(this.lightAndTintPosition)
    }
}