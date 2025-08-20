package me.luligabi.yet_another_industrialization.datagen

import net.minecraft.data.DataProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

interface DatagenEntrypoint {

    fun GatherDataEvent.add(provider: (GatherDataEvent) -> DataProvider) {
        generator.addProvider(includeClient(), provider.invoke(this))
    }

}