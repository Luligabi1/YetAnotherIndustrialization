package me.luligabi.yet_another_industrialization.client

import net.swedz.tesseract.neoforge.config.annotation.ConfigComment
import net.swedz.tesseract.neoforge.config.annotation.ConfigKey
import net.swedz.tesseract.neoforge.config.annotation.SubSection

interface YAIClientConfig {

    @ConfigKey("multiblock_model")
    @SubSection
    fun multiblockModel(): MultiblockModel

    interface MultiblockModel {

        @ConfigKey("disableModelCache")
        @ConfigComment("Disable model cache")
        fun disableModelCache() = false

        @ConfigKey("minimalQuads")
        @ConfigComment("Use minimal quads for rendering")
        fun minimalQuads() = false

    }

}