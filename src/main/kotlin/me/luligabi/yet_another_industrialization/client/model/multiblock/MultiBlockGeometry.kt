package me.luligabi.yet_another_industrialization.client.model.multiblock

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import me.luligabi.yet_another_industrialization.common.model.multiblock.MultiBlockGeometryBase
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ModelState
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry
import java.util.function.Function

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockGeometry : MultiBlockGeometryBase, IUnbakedGeometry<MultiBlockGeometry> {
    constructor(scaleToBlocks: Int, version: Int, blocks: MutableMap<BlockPos, Voxel>, lightEmission: Int) : super(
        scaleToBlocks,
        version,
        blocks,
        lightEmission
    )

    constructor(
        version: Int,
        ref: Map<String, BlockState>,
        shape: List<List<String>>,
        scaleToBlocks: Int,
        lightEmission: Int
    ) : super(version, ref, shape, scaleToBlocks, lightEmission)

    override fun bake(
        iGeometryBakingContext: IGeometryBakingContext,
        modelBaker: ModelBaker,
        spriteGetter: Function<Material?, TextureAtlasSprite?>,
        modelState: ModelState,
        itemOverrides: ItemOverrides
    ): BakedModel {
        return MultiBlockModel(this)
    }

    companion object {
        val EMPTY: MultiBlockGeometry =
            MultiBlockGeometry(0, mutableMapOf<String, BlockState>(), mutableListOf<MutableList<String>>(), 1, 0)
        val CODEC: MapCodec<MultiBlockGeometry> =
            RecordCodecBuilder.mapCodec<MultiBlockGeometry>(Function { instance: RecordCodecBuilder.Instance<MultiBlockGeometry> ->
                instance.group<Int, MutableMap<String, BlockState>, MutableList<MutableList<String>>, Int, Int>(
                    Codec.INT.fieldOf("version").forGetter(MultiBlockGeometry::version),
                    Codec.unboundedMap<String, BlockState>(Codec.STRING, BlockState.CODEC).fieldOf("ref")
                        .forGetter(MultiBlockGeometry::ref),
                    Codec.list(Codec.list(Codec.STRING)).fieldOf("shape")
                        .forGetter(MultiBlockGeometry::shape),
                    Codec.INT.optionalFieldOf("scaleToBlocks", 1).forGetter(MultiBlockGeometry::scaleToBlocks),
                    Codec.INT.optionalFieldOf("lightEmission", 0).forGetter(MultiBlockGeometry::lightEmission)
                ).apply<MultiBlockGeometry>(instance, ::MultiBlockGeometry)
            })

        val STREAM_CODEC: StreamCodec<ByteBuf, MultiBlockGeometry> = StreamCodec.composite(
            ByteBufCodecs.INT, MultiBlockGeometry::version,
            ByteBufCodecs.map(
                ::HashMap,
                ByteBufCodecs.STRING_UTF8,
                ByteBufCodecs.fromCodec(BlockState.CODEC)
            ), MultiBlockGeometry::ref,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), MultiBlockGeometry::shape,
            ByteBufCodecs.INT, MultiBlockGeometry::scaleToBlocks,
            ByteBufCodecs.INT, MultiBlockGeometry::lightEmission,
            ::MultiBlockGeometry
        )
    }
}