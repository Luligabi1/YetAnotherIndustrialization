package me.luligabi.yet_another_industrialization.client.model.multiblock

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.color.block.BlockColors
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import kotlin.math.min

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockRenderer(
    pBlockColors: BlockColors,
    var realLevel: BlockAndTintGetter,
    var lightAndTintPosition: BlockPos,
    isItemRender: Boolean
) : ModelBlockRenderer(pBlockColors) {
    var isItemRender: Boolean = false

    init {
        this.isItemRender = isItemRender
    }

    override fun putQuadData(
        level: BlockAndTintGetter,
        state: BlockState,
        pos: BlockPos,
        consumer: VertexConsumer,
        pose: PoseStack.Pose,
        quad: BakedQuad,
        brightness0: Float,
        brightness1: Float,
        brightness2: Float,
        brightness3: Float,
        lightmap0: Int,
        lightmap1: Int,
        lightmap2: Int,
        lightmap3: Int,
        packedOverlay: Int
    ) {
        if (quad is MultiBlockBakedQuad) {
            var r = 1.0f
            var g = 1.0f
            var b = 1.0f
            if (quad.isTinted()) {
                val i = this.blockColors.getColor(quad.state, level, pos, quad.getTintIndex())
                r = (i shr 16 and 0xFF).toFloat() / 255.0f
                g = (i shr 8 and 0xFF).toFloat() / 255.0f
                b = (i and 0xFF).toFloat() / 255.0f
            }
            consumer.putBulkData(
                pose,
                quad,
                floatArrayOf(brightness0, brightness1, brightness2, brightness3),
                r,
                g,
                b,
                1.0f,
                intArrayOf(lightmap0, lightmap1, lightmap2, lightmap3),
                packedOverlay,
                true
            )
            return
        }

        super.putQuadData(
            level, state, pos,
            consumer, pose,
            quad,
            brightness0, brightness1, brightness2, brightness3,
            lightmap0, lightmap1, lightmap2, lightmap3,
            packedOverlay
        )
    }

    override fun renderModelFaceAO(
        pLevel: BlockAndTintGetter,
        pState: BlockState,
        pPos: BlockPos,
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        pQuads: MutableList<BakedQuad>,
        pShape: FloatArray,
        pShapeFlags: BitSet,
        pAoFace: AmbientOcclusionFace,
        pPackedOverlay: Int
    ) {
        val shape = FloatArray(12)
        val shapeFlags = BitSet(3)
        shapeFlags.set(1, false)

        val levelColor = LevelRenderer.getLightColor(realLevel, lightAndTintPosition)
        for (bakedquad in pQuads) {
            var state = pState
            var pos = pPos
            var brightness = pAoFace.brightness
            var lightmap = pAoFace.lightmap
            if (bakedquad is MultiBlockBakedQuad) {
                state = bakedquad.state
                pos = bakedquad.pos

                if (bakedquad.aoFace == null) {
                    val face = MultiBlockAmbientOcclusionFace()
                    face.calculate(
                        pLevel,
                        state,
                        pos,
                        bakedquad.getDirection(),
                        shape,
                        shapeFlags,
                        bakedquad.isShade()
                    )
                    bakedquad.aoFace = face
                }
                brightness = bakedquad.aoFace!!.brightness
                lightmap = bakedquad.aoFace!!.lightmap
            }


            val b1 = min(1.0f, brightness[0] + 0.2f)
            val b2 = min(1.0f, brightness[1] + 0.2f)
            val b3 = min(1.0f, brightness[2] + 0.2f)
            val b4 = min(1.0f, brightness[3] + 0.2f)
            var l1 = lightmap[0] + levelColor shr 1
            var l2 = lightmap[1] + levelColor shr 1
            var l3 = lightmap[2] + levelColor shr 1
            var l4 = lightmap[3] + levelColor shr 1
            if (isItemRender) {
                l1 = 0xFF00FF
                l2 = 0xFF00FF
                l3 = 0xFF00FF
                l4 = 0xFF00FF
            }
            this.putQuadData(
                pLevel, state!!, pos!!, pConsumer, pPoseStack.last(), bakedquad,
                b1, b2, b3, b4,
                l1, l2, l3, l4,
                pPackedOverlay
            )
        }
    }

    override fun renderModelFaceFlat(
        pLevel: BlockAndTintGetter,
        pState: BlockState,
        pPos: BlockPos,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pRepackLight: Boolean,
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        pQuads: MutableList<BakedQuad>,
        pShapeFlags: BitSet
    ) {
        var pPackedLight = pPackedLight
        val shape = FloatArray(12)
        val shapeFlags = BitSet(3)

        for (bakedquad in pQuads) {
            var state = pState
            var pos = pPos
            if (bakedquad is MultiBlockBakedQuad) {
                state = bakedquad.state
                pos = bakedquad.pos
            }

            if (pRepackLight) {
                this.calculateShape(
                    pLevel,
                    state,
                    pos,
                    bakedquad.getVertices(),
                    bakedquad.getDirection(),
                    shape,
                    shapeFlags
                )
                val blockpos: BlockPos = (if (shapeFlags.get(0)) pos.relative(bakedquad.getDirection()) else pos)!!
                pPackedLight = LevelRenderer.getLightColor(pLevel, state, blockpos)
            }

            val f = pLevel.getShade(bakedquad.getDirection(), bakedquad.isShade())
            this.putQuadData(
                pLevel,
                state,
                pos,
                pConsumer,
                pPoseStack.last(),
                bakedquad,
                f,
                f,
                f,
                f,
                pPackedLight,
                pPackedLight,
                pPackedLight,
                pPackedLight,
                pPackedOverlay
            )
        }
    }
}