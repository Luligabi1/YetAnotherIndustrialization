package me.luligabi.yet_another_industrialization.client.model.multiblock

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockBakedQuad private constructor(
    pVertices: IntArray,
    pTintIndex: Int,
    pDirection: Direction,
    pSprite: TextureAtlasSprite,
    pShade: Boolean,
    val state: BlockState,
    val pos: BlockPos
) : BakedQuad(
    pVertices,
    pTintIndex,
    pDirection,
    pSprite,
    pShade,
    true
) {

    var aoFace: MultiBlockAmbientOcclusionFace? = null

    companion object {
        fun of(quad: BakedQuad, state: BlockState, pos: BlockPos): MultiBlockBakedQuad {
            return MultiBlockBakedQuad(
                quad.getVertices(),
                quad.getTintIndex(),
                quad.getDirection(),
                quad.getSprite(),
                quad.isShade,
                state,
                pos
            )
        }

        fun copy(quad: MultiBlockBakedQuad): MultiBlockBakedQuad {
            val vertices = quad.getVertices()
            return MultiBlockBakedQuad(
                vertices.copyOf(vertices.size),
                quad.getTintIndex(),
                quad.getDirection(),
                quad.getSprite(),
                quad.isShade,
                quad.state,
                quad.pos
            )
        }
    }
}