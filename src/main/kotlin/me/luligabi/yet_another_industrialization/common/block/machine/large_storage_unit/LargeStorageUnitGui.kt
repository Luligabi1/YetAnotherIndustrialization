package me.luligabi.yet_another_industrialization.common.block.machine.large_storage_unit

import aztech.modern_industrialization.machines.gui.GuiComponent
import me.luligabi.yet_another_industrialization.common.YAI
import net.minecraft.network.RegistryFriendlyByteBuf

object LargeStorageUnitGui {

    val ID = YAI.id(LargeStorageUnitBlockEntity.ID)

    class Server(
        private val isShapeValid: () -> Boolean,
        private val euSupplier: () -> Long,
        private val maxEuSupplier: () -> Long
    ) : GuiComponent.Server<Data> {

        override fun copyData(): Data {
            if (!isShapeValid()) return Data(false, euSupplier(), -1)
            return Data(true, euSupplier(), maxEuSupplier())
        }

        override fun needsSync(cachedData: Data): Boolean {
            return cachedData.isShapeValid != isShapeValid() ||
                    cachedData.stored != euSupplier() ||
                    cachedData.capacity != maxEuSupplier()
        }

        override fun writeInitialData(buf: RegistryFriendlyByteBuf) {
            writeCurrentData(buf)
        }

        override fun writeCurrentData(buf: RegistryFriendlyByteBuf) {
            buf.writeBoolean(isShapeValid())
            buf.writeLong(euSupplier())
            buf.writeLong(maxEuSupplier())
        }

        override fun getId() = ID
    }

    data class Data(val isShapeValid: Boolean, val stored: Long, val capacity: Long)
}
