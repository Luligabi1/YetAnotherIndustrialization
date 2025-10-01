package me.luligabi.yet_another_industrialization.client.model.multiblock

import com.mojang.math.Transformation
import me.luligabi.yet_another_industrialization.common.model.multiblock.MultiBlockGeometryBase
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.ChunkRenderTypeSet
import net.neoforged.neoforge.client.model.IDynamicBakedModel
import net.neoforged.neoforge.client.model.IQuadTransformer
import net.neoforged.neoforge.client.model.QuadTransformers
import net.neoforged.neoforge.client.model.data.ModelData
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.max

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockModel(val geometry: MultiBlockGeometry) : IDynamicBakedModel {

    private companion object {
         val MISSING_TEXTURE = Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation())
    }

    private val cache: MutableMap<Direction?, MutableList<BakedQuad>> = HashMap()

    private val scaleToBlocks = geometry.scaleToBlocks
    private val scale: Float

    val blocks = geometry.voxels

    init {

        var width = 0
        var height = 0
        var depth = 0
        for (pos in blocks.keys) {
            if (pos.x > width) {
                width = pos.x
            }
            if (pos.y > height) {
                height = pos.y
            }
            if (pos.z > depth) {
                depth = pos.z
            }
        }

        var dim = max(height, max(width, depth))

        ++dim
        if (height > 6 || dim <= 4) {
            dim = max(6, dim)
        }

        this.scale = this.scaleToBlocks / dim.toFloat()
    }

    override fun getQuads(
        blockState: BlockState?,
        direction: Direction?,
        randomSource: RandomSource,
        modelData: ModelData,
        renderType: RenderType?
    ): List<BakedQuad> {
        if (cache.isEmpty() || !cache.containsKey(direction) || false /* TODO ClientConfig.disableModelCache*/) {
            cache[direction] = ArrayList()

            val centerOffset = Vector3f(geometry.trunkPos.x + 0.5f, 0f, geometry.trunkPos.z + 0.5f)
            for (voxel in blocks.values) {
                if (false /* TODO ClientConfig.minimalQuads*/ && direction != null && blocks.containsKey(voxel.pos.relative(direction))) {
                    val relativeState: MultiBlockGeometryBase.Voxel? = blocks[voxel.pos.relative(direction)]
                    if (relativeState != null && relativeState.state.block == voxel.state.block) {
                        continue
                    }
                }

                val modelQuads: List<BakedQuad> = voxel.model().getQuads(voxel.state, direction, randomSource, modelData, null)
                val translate = Transformation(Matrix4f().translate(voxel.pos.x.toFloat(), voxel.pos.y.toFloat(), voxel.pos.z.toFloat()))
                val translateCenter = Transformation(Matrix4f().translate(-centerOffset.x, 0f, -centerOffset.z))
                val scale = Transformation(Matrix4f().scale(this.scale))

                val translator: IQuadTransformer = QuadTransformers.applying(translate)
                val centerer: IQuadTransformer = QuadTransformers.applying(translateCenter)
                val scaler: IQuadTransformer = QuadTransformers.applying(scale)

                val transformedQuads = translator.andThen(centerer).andThen(scaler).process(modelQuads)
                for (quad in transformedQuads) {
                    cache[direction]!!.add(MultiBlockBakedQuad.of(quad, voxel.state, voxel.pos))
                }
            }
        }

        return cache[direction]!!
    }

    override fun useAmbientOcclusion() = true

    override fun isGui3d() = false

    override fun usesBlockLight() = false

    override fun isCustomRenderer() = false

    override fun getParticleIcon() = MISSING_TEXTURE.sprite()

    override fun getRenderTypes(state: BlockState, rand: RandomSource, data: ModelData): ChunkRenderTypeSet {
        return ChunkRenderTypeSet.of(RenderType.TRANSLUCENT)
    }

    override fun getOverrides(): ItemOverrides {
        return ItemOverrides.EMPTY
    }
}