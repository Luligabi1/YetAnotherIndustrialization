package me.luligabi.yet_another_industrialization.common.compat.guideme

import aztech.modern_industrialization.guidebook.MultiblockShapeCompiler
import guideme.Guide
import guideme.scene.element.SceneElementTagCompiler
import me.luligabi.yet_another_industrialization.common.YAI

object YAIGuide {

    val ID = YAI.id("guide")

    val GUIDE = Guide.builder(ID)
        .folder("yai_guidebook")
        .extension(SceneElementTagCompiler.EXTENSION_POINT, MultiblockShapeCompiler())
        .build()

}