package me.luligabi.yet_another_industrialization.client.model.multiblock

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import me.luligabi.yet_another_industrialization.common.YAI
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader

/**
 * All rights reserved davenonymous 2025
 * Source code (java) is available at https://github.com/davenonymous/BonsaiTrees
 *
 * Graciously allowed to use in this project. Thanks, Dave!
 * Proof: https://web.archive.org/web/20250909180439/https://github.com/davenonymous/BonsaiTrees/issues/375
 */
class MultiBlockModelLoader private constructor() : IGeometryLoader<MultiBlockGeometry> {

    companion object {
        val INSTANCE = MultiBlockModelLoader()

        val ID = YAI.id("multiblock")
    }

    override fun read(jsonObject: JsonObject, deserializationContext: JsonDeserializationContext): MultiBlockGeometry {
        return MultiBlockGeometry.CODEC.codec().parse(JsonOps.INSTANCE, jsonObject).result()
            .orElse(MultiBlockGeometry.EMPTY)!!
    }

}