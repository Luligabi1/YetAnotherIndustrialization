package me.luligabi.yet_another_industrialization.common.model.multiblock

import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
open class MultiBlockGeometryBase(
	val version: Int,
	val ref: Map<String, BlockState>,
	val shape: List<List<String>>,
	val scaleToBlocks: Int,
	val lightEmission: Int
) {

	companion object {
		val EMPTY = MultiBlockGeometryBase(4, emptyMap(), emptyList(), 1, 0)

		val CODEC: MapCodec<MultiBlockGeometryBase> = RecordCodecBuilder.mapCodec { instance ->
			instance.group(
				Codec.INT.fieldOf("version").forGetter(MultiBlockGeometryBase::version),
				Codec.unboundedMap(Codec.STRING, BlockState.CODEC).fieldOf("ref").forGetter(MultiBlockGeometryBase::ref),
				Codec.list(Codec.list(Codec.STRING)).fieldOf("shape").forGetter(MultiBlockGeometryBase::shape),
				Codec.INT.optionalFieldOf("scaleToBlocks", 1).forGetter(MultiBlockGeometryBase::scaleToBlocks),
				Codec.INT.optionalFieldOf("lightEmission", 0).forGetter(MultiBlockGeometryBase::lightEmission)
			).apply(instance, ::MultiBlockGeometryBase)
		}

		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MultiBlockGeometryBase> = StreamCodec.composite(
			ByteBufCodecs.INT, MultiBlockGeometryBase::version,
			ByteBufCodecs.map(::HashMap, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC)), MultiBlockGeometryBase::ref,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), MultiBlockGeometryBase::shape,
			ByteBufCodecs.INT, MultiBlockGeometryBase::scaleToBlocks,
			ByteBufCodecs.INT, MultiBlockGeometryBase::lightEmission,
			::MultiBlockGeometryBase
		)

		fun castVoxelMap(blocks: Map<BlockPos, BlockState>): Map<BlockPos, Voxel> {
			val voxels = HashMap<BlockPos, Voxel>()
			for ((pos, state) in blocks) {
				voxels[pos] = Voxel(pos, state)
			}
			return voxels
		}

		fun forDataGen(blocks: Map<BlockPos, BlockState>, lightEmission: Int): MultiBlockGeometryBase {
			return MultiBlockGeometryBase(1, 4, castVoxelMap(blocks), lightEmission)
		}
	}

	val voxels = getBlocks()
	val trunkPos = getTrunkPosition()

	constructor(scaleToBlocks: Int, version: Int, blocks: Map<BlockPos, Voxel>, lightEmission: Int) : this(
		version = version,
		ref = mutableMapOf<String, BlockState>().let {
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

			val refMap = HashMap<BlockState, Char>()
			var refChar = 'a'

			val blocksAsArray = Array(width + 1) { Array(height + 1) { CharArray(depth + 1) } }
			for (x in 0..width) {
				for (y in 0..height) {
					for (z in 0..depth) {
						blocksAsArray[x][y][z] = ' '
					}
				}
			}

			for ((pos, voxel) in blocks.entries.sortedBy { it.key }) {
				val state = voxel.state

				if (!refMap.containsKey(state)) {
					refMap[state] = refChar++
					it[refChar.toString()] = state
					if (refChar.code == 'z'.code + 1) {
						refChar = 'A'
					}
				}

				val stateChar = refMap[state]!!
				blocksAsArray[pos.x][pos.y][pos.z] = stateChar
			}

			it
		},
		shape = mutableMapOf<BlockState, Char>().let {
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

			var refChar = 'a'

			val blocksAsArray = Array(width + 1) { Array(height + 1) { CharArray(depth + 1) } }
			for (x in 0..width) {
				for (y in 0..height) {
					for (z in 0..depth) {
						blocksAsArray[x][y][z] = ' '
					}
				}
			}

			for ((pos, voxel) in blocks.entries.sortedBy { it.key }) {
				val state = voxel.state

				if (!it.containsKey(state)) {
					it[state] = refChar++
					if (refChar.code == 'z'.code + 1) {
						refChar = 'A'
					}
				}

				val stateChar = it[state]!!
				blocksAsArray[pos.x][pos.y][pos.z] = stateChar
			}

			blocksAsArray.map { x -> x.map { it.concatToString() } }
		},
		scaleToBlocks = scaleToBlocks,
		lightEmission = lightEmission
	)

	fun getBlockCount(): Int {
		return voxels.keys.size
	}

	fun getStateCount(): Int {
		return ref.keys.size
	}

	private fun getTrunkPosition(): BlockPos {
		val size = getSize()
		val center = BlockPos(size.x / 2, 0, size.z / 2)

		val roots = mutableListOf<BlockPos>()
		if (voxels.containsKey(center)) {
			roots.add(center)
		}

		for (testPos in BlockPos.spiralAround(center, 3, Direction.EAST, Direction.NORTH)) {
			if (voxels.containsKey(testPos)) {
				val state = voxels[testPos]!!.state
				if (state.isAir || state.`is`(Blocks.WATER) || state.`is`(BlockTags.LEAVES)) {
					continue
				}
				roots.add(BlockPos(testPos.x, 0, testPos.z))
			}
		}

		if (roots.isEmpty()) {
			return center
		}

		if (roots.size == 1) {
			return roots.first()
		}

		if (roots.size >= 4) {
			return center
		}

		// Otherwise use the center of the roots
		var x = 0
		var z = 0
		for (root in roots) {
			x += root.x
			z += root.z
		}
		val newCenter = BlockPos(x / roots.size, 0, z / roots.size)
		return newCenter
	}

	private fun getBlocks(): Map<BlockPos, Voxel> {
		val blocks = HashMap<BlockPos, Voxel>()
		for (x in shape.indices) {
			val slice = shape[x]
			for (y in slice.size - 1 downTo 0) {
				val row = slice[y]
				for (z in row.indices) {
					val key = row.substring(z, z + 1)
					if (key == " ") {
						continue
					}
					val state = ref[key]
					if (state != null) {
						val pos = BlockPos(shape.size - 1 - x, slice.size - 1 - y, z)
						blocks[pos] = Voxel(pos, state)
					}
				}
			}
		}

		return blocks
	}

	fun getSize(): Vec3i {
		var maxX = 0
		var maxY = 0
		var maxZ = 0
		for (voxel in voxels.values) {
			val pos = voxel.pos
			maxX = max(maxX, pos.x)
			maxY = max(maxY, pos.y)
			maxZ = max(maxZ, pos.z)
		}
		return Vec3i(maxX, maxY, maxZ)
	}

	fun getMaxDimension(): Int {
		val size = getSize()
		return max(size.x, max(size.y, size.z))
	}

	fun getMinDimension(): Int {
		val size = getSize()
		return min(size.x, min(size.y, size.z))
	}

	override fun equals(other: Any?): Boolean {
		if (other === this) {
			return true
		}
		if (other == null || other.javaClass != this.javaClass) {
			return false
		}
		val that = other as MultiBlockGeometryBase
		return this.version == that.version &&
				this.ref == that.ref &&
				this.shape == that.shape &&
				this.scaleToBlocks == that.scaleToBlocks
	}

	override fun hashCode(): Int {
		return Objects.hash(version, ref, shape, scaleToBlocks)
	}

	override fun toString(): String {
		return "MultiBlockModelGeometry[" +
				"version=$version, " +
				"ref=$ref, " +
				"shape=$shape, " +
				"scaleToBlocks=$scaleToBlocks]"
	}

	fun serializePretty(): String {
		if (getSize().x + 1 == 0 || getSize().y + 1 == 0 || getSize().z + 1 == 0) {
			return ""
		}

		val map = Array(getSize().x + 1) { Array(getSize().y + 1) { CharArray(getSize().z + 1) } }
		val refMapBuilder = StringBuilder()
		refMapBuilder.append("  \"ref\": {\n")
		var nextRef = 'a'
		val refMap = HashMap<String, Char>()

		for ((pos, voxel) in this.voxels) {
			val state = voxel.state

			val jsonString = BlockState.CODEC.encodeStart(JsonOps.INSTANCE, state).result().get().toString()

			// Get new or already used reference char for this block
			val thisRef: Char
			if (refMap.containsKey(jsonString)) {
				thisRef = refMap[jsonString]!!
			} else {
				thisRef = nextRef++
				if (nextRef.code == 'z'.code + 1) {
					nextRef = 'A'
				}
				refMap[jsonString] = thisRef

				refMapBuilder.append("    \"$thisRef\": $jsonString,\n")
			}

			map[pos.x][pos.y][pos.z] = thisRef
		}
		refMapBuilder.deleteCharAt(refMapBuilder.length - 2)
		refMapBuilder.append("  },\n")

		val output = StringBuilder("{\n")

		output.append("  \"loader\": \"bonsaitrees4:multiblockmodel\",\n")
		output.append("  \"version\": 4,\n")
		output.append(refMapBuilder)
		output.append("  \"shape\": [\n")

		for (x in map.size - 1 downTo 0) {
			output.append("    [\n")
			for (y in map[x].size - 1 downTo 0) {
				val builder = StringBuilder()
				for (z in map[x][y].indices) {
					var chr = ' '
					if (map[x][y][z] != '\u0000') {
						chr = map[x][y][z]
					}
					builder.append(chr)
				}

				output.append("      \"$builder\",\n")
			}
			output.deleteCharAt(output.length - 2)
			output.append("    ],\n")
		}
		output.deleteCharAt(output.length - 2)

		output.append("  ]\n}\n")

		return output.toString()
	}

	data class Voxel(val pos: BlockPos, val state: BlockState) {
		fun model(): BakedModel {
			return Minecraft.getInstance().modelManager.blockModelShaper.getBlockModel(state)
		}
	}
}