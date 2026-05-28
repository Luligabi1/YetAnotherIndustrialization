package me.luligabi.yet_another_industrialization.client

import net.swedz.tesseract.config.annotation.ConfigComment
import net.swedz.tesseract.config.annotation.ConfigKey
import net.swedz.tesseract.config.annotation.SubSection

interface YAIClientConfig {

    @ConfigKey("multiblock_model")
    @SubSection
    fun multiblockModel(): MultiblockModel

    @ConfigKey("flight_pylon")
    @SubSection
    fun flightPylon(): FlightPylon

    interface MultiblockModel {

        @ConfigKey("disableModelCache")
        @ConfigComment("Disable model cache")
        fun disableModelCache() = false

        @ConfigKey("minimalQuads")
        @ConfigComment("Use minimal quads for rendering")
        fun minimalQuads() = false

    }

    interface FlightPylon {

        @ConfigKey("disableBeacon")
        @ConfigComment("Disable beacon rendering regardless of pylon's state")
        fun disableBeacon() = false

    }

}