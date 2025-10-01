package me.luligabi.yet_another_industrialization.client.model.multiblock

import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.block.ModelBlockRenderer.AdjacencyInfo
import net.minecraft.client.renderer.block.ModelBlockRenderer.AmbientVertexRemap
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import java.util.*

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockAmbientOcclusionFace {
    val brightness: FloatArray = FloatArray(4)
    val lightmap: IntArray = IntArray(4)

    fun calculate(
        level: BlockAndTintGetter,
        state: BlockState,
        pos: BlockPos,
        direction: Direction,
        shape: FloatArray,
        shapeFlags: BitSet,
        shade: Boolean
    ) {
        val blockpos = if (shapeFlags.get(0)) pos.relative(direction) else pos
        val adjacent = AdjacencyInfo.fromFacing(direction)
        val nowPos = MutableBlockPos()
        nowPos.setWithOffset(blockpos, adjacent.corners[0])
        val blockstate = level.getBlockState(nowPos)

        val i = LevelRenderer.getLightColor(level, blockstate, nowPos)
        val f = blockstate.getShadeBrightness(level, nowPos)
        nowPos.setWithOffset(blockpos, adjacent.corners[1])
        val blockstate1 = level.getBlockState(nowPos)
        val j = LevelRenderer.getLightColor(level, blockstate1, nowPos)
        val f1 = blockstate1.getShadeBrightness(level, nowPos)
        nowPos.setWithOffset(blockpos, adjacent.corners[2])
        val blockstate2 = level.getBlockState(nowPos)
        val k = LevelRenderer.getLightColor(level, blockstate2, nowPos)
        val f2 = blockstate2.getShadeBrightness(level, nowPos)
        nowPos.setWithOffset(blockpos, adjacent.corners[3])
        val blockstate3 = level.getBlockState(nowPos)
        val l = LevelRenderer.getLightColor(level, blockstate3, nowPos)
        val f3 = blockstate3.getShadeBrightness(level, nowPos)
        val blockstate4 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[0]))
        val flag = !blockstate4.isViewBlocking(level, nowPos) || blockstate4.getLightBlock(level, nowPos) == 0
        val blockstate5 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[1]))
        val flag1 = !blockstate5.isViewBlocking(level, nowPos) || blockstate5.getLightBlock(level, nowPos) == 0
        val blockstate6 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[2]))
        val flag2 = !blockstate6.isViewBlocking(level, nowPos) || blockstate6.getLightBlock(level, nowPos) == 0
        val blockstate7 = level.getBlockState(nowPos.setWithOffset(blockpos, adjacent.corners[3]))
        val flag3 = !blockstate7.isViewBlocking(level, nowPos) || blockstate7.getLightBlock(level, nowPos) == 0
        val f4: Float
        val i1: Int
        if (!flag2 && !flag) {
            f4 = f
            i1 = i
        } else {
            nowPos.setWithOffset(blockpos, adjacent.corners[0]).move(adjacent.corners[2])
            val blockstate8 = level.getBlockState(nowPos)
            f4 = blockstate8.getShadeBrightness(level, nowPos)
            i1 = LevelRenderer.getLightColor(level, blockstate8, nowPos)
        }

        val j1: Int
        val f5: Float
        if (!flag3 && !flag) {
            f5 = f
            j1 = i
        } else {
            nowPos.setWithOffset(blockpos, adjacent.corners[0]).move(adjacent.corners[3])
            val blockstate10 = level.getBlockState(nowPos)
            f5 = blockstate10.getShadeBrightness(level, nowPos)
            j1 = LevelRenderer.getLightColor(level, blockstate10, nowPos)
        }

        val k1: Int
        val f6: Float
        if (!flag2 && !flag1) {
            f6 = f
            k1 = i
        } else {
            nowPos.setWithOffset(blockpos, adjacent.corners[1]).move(adjacent.corners[2])
            val blockstate11 = level.getBlockState(nowPos)
            f6 = blockstate11.getShadeBrightness(level, nowPos)
            k1 = LevelRenderer.getLightColor(level, blockstate11, nowPos)
        }

        val l1: Int
        val f7: Float
        if (!flag3 && !flag1) {
            f7 = f
            l1 = i
        } else {
            nowPos.setWithOffset(blockpos, adjacent.corners[1]).move(adjacent.corners[3])
            val blockstate12 = level.getBlockState(nowPos)
            f7 = blockstate12.getShadeBrightness(level, nowPos)
            l1 = LevelRenderer.getLightColor(level, blockstate12, nowPos)
        }

        var i3 = LevelRenderer.getLightColor(level, state, nowPos)
        nowPos.setWithOffset(pos, direction)
        val blockstate9 = level.getBlockState(nowPos)
        if (shapeFlags.get(0) || !blockstate9.isSolidRender(level, nowPos)) {
            i3 = LevelRenderer.getLightColor(level, blockstate9, nowPos)
        }
        i3 = 0

        val f8 = if (shapeFlags.get(0))
            level.getBlockState(blockpos).getShadeBrightness(level, blockpos)
        else
            level.getBlockState(pos).getShadeBrightness(level, pos)
        val vertexRemap = AmbientVertexRemap.fromFacing(direction)

        val f30: Float = (f3 + f + f5 + f8) * 0.25f
        val f10: Float = (f2 + f + f4 + f8) * 0.25f
        val f11: Float = (f2 + f1 + f6 + f8) * 0.25f
        val f12: Float = (f3 + f1 + f7 + f8) * 0.25f
        this.lightmap[vertexRemap.vert0] = this.blend(l, i, j1, i3)
        this.lightmap[vertexRemap.vert1] = this.blend(k, i, i1, i3)
        this.lightmap[vertexRemap.vert2] = this.blend(k, j, k1, i3)
        this.lightmap[vertexRemap.vert3] = this.blend(l, j, l1, i3)
        this.brightness[vertexRemap.vert0] = f30
        this.brightness[vertexRemap.vert1] = f10
        this.brightness[vertexRemap.vert2] = f11
        this.brightness[vertexRemap.vert3] = f12

        //		f30 = level.getShade(direction, shade);
        //
        //		for(int j3 = 0; j3 < this.brightness.length; ++j3) {
        //			this.brightness[j3] *= f30;
        //		}
    }

    private fun blend(lightColor0: Int, lightColor1: Int, lightColor2: Int, lightColor3: Int): Int {
        var lightColor0 = lightColor0
        var lightColor1 = lightColor1
        var lightColor2 = lightColor2
        if (lightColor0 == 0) {
            lightColor0 = lightColor3
        }

        if (lightColor1 == 0) {
            lightColor1 = lightColor3
        }

        if (lightColor2 == 0) {
            lightColor2 = lightColor3
        }

        return lightColor0 + lightColor1 + lightColor2 + lightColor3 shr 2 and 16711935
    }
}